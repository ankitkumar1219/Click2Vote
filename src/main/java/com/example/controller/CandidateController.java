package com.example.controller;

import com.example.entity.Candidate;
import com.example.service.CandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    // ➕ Add Candidate (only admin should do this)
    @PostMapping("/add")
    public Candidate addCandidate(@RequestBody Candidate candidate) {
        return candidateService.addCandidate(candidate);
    }

    // 👀 View All Candidates
    @GetMapping("/all")
    public List<Candidate> getAllCandidates() {
        return candidateService.getAllCandidates();
    }

    // 📊 View Results (votes count)
    @GetMapping("/results")
    public List<Candidate> getResults() {
        // returns same as /all but frontend will display votes
        return candidateService.getAllCandidates();
    }

    // ❌ Delete Candidate
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCandidate(@PathVariable Long id) {
        boolean deleted = candidateService.deleteCandidate(id);
        if (deleted) {
            return ResponseEntity.ok("✅ Candidate deleted successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Candidate not found");
        }
    }
}
