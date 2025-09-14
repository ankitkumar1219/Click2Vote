package com.example.controller;

import com.example.entity.Candidate;
import com.example.entity.Voter;
import com.example.service.CandidateService;
import com.example.service.ElectionService;
import com.example.service.VoterService;
import com.example.service.MailService; // <- import your MailService
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/voters")
public class VoterController {

    private final VoterService voterService;
    private final CandidateService candidateService;
    private final MailService mailService;
    private final ElectionService electionService; // ✅ Inject ElectionService

    public VoterController(VoterService voterService, 
                           CandidateService candidateService, 
                           MailService mailService,
                           ElectionService electionService) {
        this.voterService = voterService;
        this.candidateService = candidateService;
        this.mailService = mailService;
        this.electionService = electionService; // ✅ assign
    }

    @PostMapping("/register")
    public Voter register(@RequestBody Voter voter) {
        return voterService.registerVoter(voter);
    }

    @PostMapping("/login")
    public Optional<Voter> login(@RequestBody Voter voter) {
        return voterService.login(voter.getEmail(), voter.getPassword());
    }

    @GetMapping("/candidates")
    public List<Candidate> getCandidates() {
        return candidateService.getAllCandidates();
    }

    // 🗳️ Cast Vote with Election Timer Check
    @PostMapping("/vote")
    public String vote(@RequestParam Long voterId, @RequestParam Long candidateId) {

        // 1️⃣ Check if voting is active
        if (!electionService.isVotingActive()) {
            return "❌ Voting is not active at this time!";
        }

        // 2️⃣ Check if voter exists
        Optional<Voter> voterOpt = voterService.getVoterById(voterId);
        if (voterOpt.isEmpty()) return "❌ Voter not found";

        Voter voter = voterOpt.get();

        // 3️⃣ Check if voter already voted
        if (voter.isVoted()) return "❌ You have already voted!";

        // 4️⃣ Check if candidate exists
        Candidate candidate = candidateService.getAllCandidates().stream()
                .filter(c -> c.getId().equals(candidateId))
                .findFirst()
                .orElse(null);
        if (candidate == null) return "❌ Candidate not found";

        // 5️⃣ Increment candidate votes
        candidate.setVotes(candidate.getVotes() + 1);
        candidateService.addCandidate(candidate);

        // 6️⃣ Mark voter as voted
        voter.setVoted(true);
        voterService.updateVoter(voter);

        return "✅ Vote cast successfully for " + candidate.getName();
    }

    @PostMapping("/sendWinnerEmail")
    public String sendWinnerEmail() {
        Candidate winner = candidateService.getAllCandidates().stream()
                .max(Comparator.comparingInt(Candidate::getVotes))
                .orElse(null);

        if (winner == null) return "❌ No candidates found";

        String subject = "Election Result!";
        String message = "🏆 Winner: " + winner.getName() + " (" + winner.getParty() +
                         ") with " + winner.getVotes() + " votes!";

        List<String> emails = voterService.getAllApprovedVoterEmails();
        for (String email : emails) {
            mailService.sendMail(email, subject, message);
        }

        return "✅ Winner email sent to all voters!";
    }
}
