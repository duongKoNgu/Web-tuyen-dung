package com.example.demo.service;

import com.example.demo.model.Candidate;
import com.example.demo.model.CandidateJob;
import com.example.demo.model.Job;
import com.example.demo.repository.CandidateJobRepository;
import com.example.demo.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CandidateJobServiceImpl implements CandidateJobService {

    @Autowired
    private CandidateJobRepository candidateJobRepository;

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private SendMailService sendMailService;

    @Override
    public String applyJob(Integer userId, Integer jobId) {
        // 1. Kiểm tra Job có tồn tại không
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc"));

        // 2. Tìm Candidate theo userId
        Candidate candidate = candidateJobRepository.findCandidateByUserId(userId);

        if (candidate == null) {
            throw new RuntimeException("Bạn chưa cập nhật thông tin cá nhân. Vui lòng cập nhật.");
        }

        // 3. Kiểm tra ứng viên đã ứng tuyển Job này chưa
        if (candidateJobRepository.checkIfAlreadyApplied(candidate.getCandidateId(), jobId)) {
            return "Bạn đã ứng tuyển công việc này rồi!";
        }

        // 4. Tạo bản ghi ứng tuyển mới
        CandidateJob application = new CandidateJob();
        application.setJob(job);
        application.setCandidate(candidate);

        candidateJobRepository.save(application);

        try {
            sendMailService.sendEmailToCandidate(userId, jobId);
        } catch (Exception e) {
            // Chỉ in lỗi ra log server để mình biết, KHÔNG ném lỗi ra ngoài
            System.err.println("⚠️ Lỗi gửi mail (nhưng vẫn ứng tuyển thành công): " + e.getMessage());
        }

        // 3. Luôn trả về thành công nếu bước 1 đã xong
        return "Ứng tuyển thành công! (Vui lòng chờ HR liên hệ)";
    }
}