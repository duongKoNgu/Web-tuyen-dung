package com.example.demo.service;

import com.example.demo.model.Candidate;
import com.example.demo.model.Job;
import com.example.demo.repository.CandidateRepository;
import com.example.demo.repository.JobRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;

@Service
public class SendMailServiceImpl implements SendMailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public String sendEmailToCandidate(Integer idCandidate, Integer idJob) {
        // Validate and get Candidate
        Optional<Candidate> candidateOpt = candidateRepository.findById(idCandidate);
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found with ID: " + idCandidate);
        }
        Candidate candidate = candidateOpt.get();

        // Validate and get Job
        Optional<Job> jobOpt = jobRepository.findById(idJob);
        if (jobOpt.isEmpty()) {
            throw new RuntimeException("Job not found with ID: " + idJob);
        }
        Job job = jobOpt.get();

        // Validate email
        if (candidate.getEmail() == null || candidate.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Candidate email is missing");
        }

        try {
            // Create MimeMessage
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(candidate.getEmail());
            helper.setSubject(" Exciting Job Opportunity - " + job.getTitle());

            // Prepare Thymeleaf context with variables
            Context context = new Context();
            context.setVariable("candidateName", candidate.getName());
            context.setVariable("candidateEmail", candidate.getEmail());
            context.setVariable("jobTitle", job.getTitle());
            context.setVariable("companyName", job.getCompanyName());
            context.setVariable("level", job.getLevel());
            context.setVariable("workingAddress", job.getWorkingAddress());

            // Process template and set as email content
            String htmlContent = templateEngine.process("email-template", context);
            helper.setText(htmlContent, true); // true = HTML format

            // Send email
            emailSender.send(mimeMessage);

            return "✅ Professional email sent successfully to " + candidate.getName() +
                    " (" + candidate.getEmail() + ")";

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }
}