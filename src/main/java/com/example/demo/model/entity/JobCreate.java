package com.example.demo.model.entity;

import lombok.Data;

import java.time.LocalDate;
@Data
public class JobCreate {


    private String companyName;

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double salaryFrom;

    private Double salaryTo;

    private String workingAddress;

    private String skills;
    private String benefits;
    private String level;
    private String description;
    private String status;

}

