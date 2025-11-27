package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data

public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Integer jobId;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String companyName;

    private String title;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "salary_from")
    private Double salaryFrom;

    @Column(name = "salary_to")
    private Double salaryTo;

    @Column(name = "working_address",columnDefinition = "NVARCHAR(255)")
    private String workingAddress;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String skills;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String benefits;

    private String level;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String status;


    private Integer createBy;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CandidateJob> candidates = new ArrayList<>();

}

