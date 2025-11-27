package com.example.demo.service;

import com.example.demo.model.Job;
import com.example.demo.model.entity.JobCreate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    public String createJob(JobCreate job, Integer CreateBy);

    public List<Job> getAllJob();

    Page<Job> getJobsWithFilters(String search, String status, Pageable pageable);

    void deleteJob(Integer id);

    public Job getJobById(Integer id);

    Page<Job> getMyJobsWithFilters(int userId, String search, String status, Pageable pageable);

    String updateJob(Integer jobId, JobCreate jobDetails, Integer userId);
}
