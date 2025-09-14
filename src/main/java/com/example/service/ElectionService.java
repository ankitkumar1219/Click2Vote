package com.example.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.entity.Election;
import com.example.repository.ElectionRepository;

@Service
public class ElectionService {
    private final ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    public Election createElection(Election election) {
        return electionRepository.save(election);
    }

    public Optional<Election> getCurrentElection() {
        LocalDateTime now = LocalDateTime.now();
        return electionRepository.findAll().stream()
                .filter(e -> now.isAfter(e.getStartTime()) && now.isBefore(e.getEndTime()))
                .findFirst();
    }

    public boolean isVotingActive() {
        Optional<Election> electionOpt = electionRepository.findTopByOrderByIdDesc(); // latest election
        if (electionOpt.isEmpty()) return false;

        Election election = electionOpt.get();
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(election.getStartTime()) && !now.isAfter(election.getEndTime());
    }

}

