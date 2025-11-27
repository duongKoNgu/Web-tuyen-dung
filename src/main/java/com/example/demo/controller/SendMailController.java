package com.example.demo.controller;

import com.example.demo.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/send-mail")
public class SendMailController {
    @Autowired
    SendMailService sendMailService;

    @GetMapping("/to-candidate")
    public String sendEmailToCandidate(@RequestParam("candidateId") String idCandidate,@RequestParam("jobId") String idJob) {
        try {

            return sendMailService.sendEmailToCandidate(Integer.valueOf(idCandidate), Integer.valueOf(idJob));
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }

}
