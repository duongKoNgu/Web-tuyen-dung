package com.example.demo.model.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
public class CandidateMto {
        private String name;
        private String gender;
        private String address;
        private String email;
        private String phone;
        private String status;
        private String bday;
        private String cv;

}
