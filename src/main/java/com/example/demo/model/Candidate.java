package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Integer candidateId;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;
    private String gender;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String address;
    private String email;
    private String phone;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String status;
    private String bday;
    private String cv;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User userCandidate;


    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CandidateJob> candidates;

}
