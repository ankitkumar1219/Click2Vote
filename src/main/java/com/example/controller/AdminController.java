package com.example.controller;

import com.example.entity.Admin;
import com.example.entity.Voter;
import com.example.service.AdminService;
import com.example.service.MailService;
import com.example.service.VoterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final VoterService voterService;
    private final MailService mailService;

    public AdminController(AdminService adminService, VoterService voterService, MailService mailService) {
        this.adminService = adminService;
        this.voterService = voterService;
        this.mailService = mailService;
    }

    // 📝 Admin Signup
    @PostMapping("/signup")
    public Admin signup(@RequestBody Admin admin) {
        return adminService.registerAdmin(admin);
    }

    // 🔑 Admin Login
 // 🔑 Admin Login
    @PostMapping("/login")
    public Optional<Admin> login(@RequestBody Admin admin) {
        return adminService.login(admin.getUsername(), admin.getPassword());
    }


    // 👀 View Pending Voters
    @GetMapping("/pending-voters")
    public List<Voter> getPendingVoters() {
        return voterService.getPendingVoters();
    }

    // ✅ Approve Voter (generate voterId + default password + email)
    @PostMapping("/approve/{voterId}")
    public String approveVoter(@PathVariable Long voterId) {
        Optional<Voter> voterOpt = voterService.getVoterById(voterId);
        if (voterOpt.isEmpty()) return "❌ Voter not found";

        Voter voter = voterOpt.get();

        String generatedVoterId = "VOTER-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String defaultPassword = "pass123"; // default password

        voter.setPassword(defaultPassword);
        voterService.approveVoter(voter, generatedVoterId);

        // Send email
        String subject = "Voter ID Approved";
        String message = "Dear " + voter.getName() +
                ",\n\nYour voter registration has been approved.\n" +
                "Voter ID: " + generatedVoterId + "\n" +
                "Password: " + defaultPassword + "\n\n" +
                "Login to cast your vote.\n\nClick2Vote Team";

        mailService.sendMail(voter.getEmail(), subject, message);

        return "✅ Voter Approved & Email Sent!";
    }

    // ❌ Reject Voter
    @PostMapping("/reject/{voterId}")
    public String rejectVoter(@PathVariable Long voterId) {
        Optional<Voter> voterOpt = voterService.getVoterById(voterId);
        if (voterOpt.isEmpty()) return "❌ Voter not found";

        voterService.rejectVoter(voterOpt.get());
        return "❌ Voter Rejected!";
    }
}
