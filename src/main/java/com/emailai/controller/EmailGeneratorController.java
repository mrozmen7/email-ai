package com.emailai.controller;


import com.emailai.dto.EmailReplyResponse;
import com.emailai.dto.EmailRequest;
import com.emailai.service.EmailGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:3000"})
public class EmailGeneratorController {

    private final EmailGeneratorService service;

    // Controller servisi constructor ile alır (Dependency Injection)
    public EmailGeneratorController(EmailGeneratorService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public ResponseEntity<EmailReplyResponse> generate(@Valid @RequestBody EmailRequest req) {
        // Artık dummy değil, gerçek AI cevabı:
        String reply = service.generateEmailReply(req);
        return ResponseEntity.ok(new EmailReplyResponse(reply));
    }
}