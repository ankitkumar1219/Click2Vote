package com.example.service;

import com.example.entity.Voter;
import com.example.repository.VoterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoterService {
    private final VoterRepository voterRepository;

    public VoterService(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    public Voter registerVoter(Voter voter) {
        if (voterRepository.findByVoterId(voter.getVoterId()).isPresent()) {
            throw new RuntimeException("❌ Voter ID already registered!");
        }
        if (voterRepository.findByEmail(voter.getEmail()).isPresent()) {
            throw new RuntimeException("❌ Email already registered!");
        }
        return voterRepository.save(voter);
    }
    public Voter updateVoter(Voter voter) {
        return voterRepository.save(voter);
    }


    public Optional<Voter> login(String email, String password) {
        return voterRepository.findByEmail(email)
                .filter(v -> v.getPassword().equals(password) && v.getStatus() == Voter.Status.APPROVED);
    }

    public List<Voter> getPendingVoters() {
        return voterRepository.findAll().stream()
                .filter(v -> v.getStatus() == Voter.Status.PENDING)
                .toList();
    }
    public Optional<Voter> getVoterById(Long id) {
        return voterRepository.findById(id);
    }


    public void approveVoter(Voter voter, String generatedVoterId) {
        voter.setStatus(Voter.Status.APPROVED);
        voter.setVoterId(generatedVoterId);
        voterRepository.save(voter);
    }

    public void rejectVoter(Voter voter) {
        voter.setStatus(Voter.Status.REJECTED);
        voterRepository.save(voter);
    }
    
 // Get all emails of approved voters
    public List<String> getAllApprovedVoterEmails() {
        return voterRepository.findAll().stream()
                .filter(v -> v.getStatus() == Voter.Status.APPROVED)
                .map(Voter::getEmail)
                .toList();
    }

    
}
