package com.example.demo.service;

import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.entity.JobCreate;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService{
    @Autowired
    JobRepository jobRepository;
    UserRepository userRepository;

    @Override
    public String createJob(JobCreate job, Integer createBy) {
        Job j = new Job();
//        User user = userRepository.findById(createBy).get();
        j.setCompanyName(job.getCompanyName());
        j.setTitle(job.getTitle());
        j.setStartDate(job.getStartDate());
        j.setEndDate(job.getEndDate());
        j.setSalaryFrom(job.getSalaryFrom());
        j.setSalaryTo(job.getSalaryTo());
        j.setWorkingAddress(job.getWorkingAddress());
        j.setSkills(job.getSkills());
        j.setBenefits(job.getBenefits());
        j.setLevel(job.getLevel());
        j.setDescription(job.getDescription());
        j.setStatus(job.getStatus());
        j.setCreateBy(createBy);
        j.setCreatedAt(LocalDate.now());
        j.setUpdatedAt(LocalDateTime.now());

        jobRepository.save(j);
        return "Tạo Job Thành Công";
    }

    @Override
    public List<Job> getAllJob() {

        return jobRepository.findAllJobs();
    }

    @Override
    public Page<Job> getJobsWithFilters(String search, String status, Pageable pageable) {
        return jobRepository.findWithFilters(search, status, pageable);
    }

    @Override
    public void deleteJob(Integer id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
        jobRepository.delete(job);
    }

    @Override
    public Job getJobById(Integer id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    @Override
    public Page<Job> getMyJobsWithFilters(int userId, String search, String status, Pageable pageable) {
        return jobRepository.findMyJobsWithFilters(userId, search, status, pageable);
    }

    @Override
    public String updateJob(Integer jobId, JobCreate jobDetails, Integer userId) {
        // 1. Tìm Job cũ
        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Job với ID: " + jobId));

        // 2. (Tùy chọn) Kiểm tra quyền sở hữu:
        // Nếu người sửa không phải người tạo (và không phải admin - logic này có thể xử lý ở Controller hoặc tại đây)
        // if (!existingJob.getCreateBy().equals(userId)) { ... }

        // 3. Cập nhật thông tin từ jobDetails sang existingJob
        existingJob.setCompanyName(jobDetails.getCompanyName());
        existingJob.setTitle(jobDetails.getTitle());
        existingJob.setStartDate(jobDetails.getStartDate());
        existingJob.setEndDate(jobDetails.getEndDate());
        existingJob.setSalaryFrom(jobDetails.getSalaryFrom());
        existingJob.setSalaryTo(jobDetails.getSalaryTo());
        existingJob.setWorkingAddress(jobDetails.getWorkingAddress());
        existingJob.setSkills(jobDetails.getSkills());
        existingJob.setBenefits(jobDetails.getBenefits());
        existingJob.setLevel(jobDetails.getLevel());
        existingJob.setDescription(jobDetails.getDescription());
        existingJob.setStatus(jobDetails.getStatus());

        // 4. Cập nhật thời gian sửa đổi
        existingJob.setUpdatedAt(LocalDateTime.now());

        // 5. Lưu lại
        jobRepository.save(existingJob);
        return "Cập nhật Job thành công!";
    }
}
