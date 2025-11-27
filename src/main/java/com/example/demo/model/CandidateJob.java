package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CandidateJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @JsonBackReference // Đã có (Tốt)
    private Job job;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    @JsonBackReference // <--- BỔ SUNG DÒNG NÀY: Ngăn vòng lặp vô tận Candidate -> Job -> Candidate
    private Candidate candidate;
}