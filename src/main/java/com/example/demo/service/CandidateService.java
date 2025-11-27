package com.example.demo.service;

import com.example.demo.model.Candidate;
import com.example.demo.model.entity.CandidateMto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CandidateService {
    Candidate saveCandidate(Candidate candidate, MultipartFile cvFile, Integer userId) throws IOException;
    List<Candidate> getCandidates();
    Candidate getCandidateById(Integer id);
    Candidate updateCandidate(Integer id, Candidate candidateDetails, MultipartFile cvFile);
    void deleteCandidate(Integer id);


    CandidateMto getCandidateByUserId(Integer userId);

    Page<Object[]> getCandidatesWithFilters(String search, String status, Pageable pageable);
    Page<Object[]> getMyCandidatesWithFilters(int userId, String search, String status, Pageable pageable);
}