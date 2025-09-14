package com.example.controller;

import com.example.entity.Election;
import com.example.service.ElectionService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/election")
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    // Set election start and end time
    @PostMapping("/set")
    public String setElection(@RequestBody Map<String, String> body) {
        try {
            String start = body.get("startTime");
            String end = body.get("endTime");

            if (start == null || end == null) return "❌ Start or End time missing";

            LocalDateTime startTime = LocalDateTime.parse(start);
            LocalDateTime endTime = LocalDateTime.parse(end);

            Election election = new Election();
            election.setStartTime(startTime);
            election.setEndTime(endTime);

            electionService.createElection(election); // ✅ Save in DB

            return "✅ Election time set successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to set election time";
        }
    }

    // Get current election times
    @GetMapping("/timer")
    public Map<String, String> getElectionTimer() {
        Map<String, String> timer = new HashMap<>();
        electionService.getCurrentElection().ifPresent(e -> {
            timer.put("startTime", e.getStartTime().toString());
            timer.put("endTime", e.getEndTime().toString());
        });
        return timer;
    }
}
