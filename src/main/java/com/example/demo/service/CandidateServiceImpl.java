package com.example.demo.service;

import com.example.demo.model.Candidate;
import com.example.demo.model.User;
import com.example.demo.model.entity.CandidateMto;
import com.example.demo.repository.CandidateRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CandidateServiceImpl implements CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    private final String uploadDir = "uploads/cv/";

    // ==================== GET CANDIDATE BY USER ID ====================

    @Override
    public CandidateMto getCandidateByUserId(Integer userId) {
        System.out.println("=== SERVICE: getCandidateByUserId ===");
        System.out.println("UserId: " + userId);

        // ✅ SỬA: Gọi đúng tên method trong repository
        Candidate candidate = candidateRepository.findByUserCandidate_IdUser(userId);
        CandidateMto candidateMto = new CandidateMto();
        candidateMto.setName(candidate.getName());
        candidateMto.setGender(candidate.getGender());
        candidateMto.setAddress(candidate.getAddress());
        candidateMto.setEmail(candidate.getEmail());
        candidateMto.setPhone(candidate.getPhone());
        candidateMto.setStatus(candidate.getStatus());
        candidateMto.setBday(candidate.getBday());
        candidateMto.setCv(candidate.getCv());

        if (candidate != null) {
            System.out.println("✅ Tìm thấy candidate ID: " + candidate.getCandidateId());
        } else {
            System.out.println("ℹ️ Không tìm thấy candidate cho userId: " + userId);
        }

        return candidateMto;
    }

    // ==================== SAVE CANDIDATE ====================

    @Override
    @Transactional
    public Candidate saveCandidate(Candidate candidate, MultipartFile cvFile, Integer userId) throws IOException {
        System.out.println("=== SERVICE: saveCandidate ===");
        System.out.println("UserId: " + userId);

        // ✅ THÊM: Gán user cho candidate
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại với ID: " + userId));
            candidate.setUserCandidate(user);

        }

        // Xử lý CV file
        if (cvFile != null && !cvFile.isEmpty()) {
            String fileName = saveCVFile(cvFile, candidate.getName());
            candidate.setCv(fileName);
            System.out.println("✅ CV đã lưu: " + fileName);
        }

        Candidate savedCandidate = candidateRepository.save(candidate);
        System.out.println("✅ Candidate saved với ID: " + savedCandidate.getCandidateId());

        return savedCandidate;
    }

    // ==================== GET CANDIDATES ====================

    @Override
    public List<Candidate> getCandidates() {
        return candidateRepository.candidates();
    }

    @Override
    public Candidate getCandidateById(Integer id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
    }

    @Override
    public Page<Object[]> getCandidatesWithFilters(String search, String status, Pageable pageable) {
        // Gọi đúng hàm repo vừa sửa
        return candidateRepository.findWithFilters(search, status, pageable);
    }

    @Override
    public Page<Object[]> getMyCandidatesWithFilters(int userId, String search, String status, Pageable pageable) {
        return candidateRepository.findMyCandidatesWithFilters(userId, search, status, pageable);
    }
    // ==================== UPDATE CANDIDATE ====================

    @Override
    @Transactional
    public Candidate updateCandidate(Integer id, Candidate candidateDetails, MultipartFile cvFile) {
        System.out.println("=== SERVICE: updateCandidate ===");
        System.out.println("Candidate ID: " + id);

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));

        // Cập nhật thông tin
        candidate.setName(candidateDetails.getName());
        candidate.setEmail(candidateDetails.getEmail());
        candidate.setPhone(candidateDetails.getPhone());
        candidate.setGender(candidateDetails.getGender());
        candidate.setBday(candidateDetails.getBday());
        candidate.setAddress(candidateDetails.getAddress());
        candidate.setStatus(candidateDetails.getStatus());

        // ✅ THÊM: Xử lý CV file mới nếu có
        if (cvFile != null && !cvFile.isEmpty()) {
            try {
                String fileName = saveCVFile(cvFile, candidate.getName());
                candidate.setCv(fileName);
                System.out.println("✅ CV mới đã lưu: " + fileName);
            } catch (IOException e) {
                System.err.println("❌ Lỗi khi lưu CV: " + e.getMessage());
                throw new RuntimeException("Không thể lưu file CV: " + e.getMessage());
            }
        }

        Candidate updatedCandidate = candidateRepository.save(candidate);
        System.out.println("✅ Candidate updated successfully");

        return updatedCandidate;
    }

    // ==================== DELETE CANDIDATE ====================

    @Override
    public void deleteCandidate(Integer id) {
        System.out.println("=== SERVICE: deleteCandidate ===");
        System.out.println("Candidate ID: " + id);

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));

        // Xóa file CV nếu có
        if (candidate.getCv() != null && !candidate.getCv().isEmpty()) {
            try {
                Path filePath = Paths.get(uploadDir + candidate.getCv());
                Files.deleteIfExists(filePath);
                System.out.println("✅ Đã xóa file CV: " + candidate.getCv());
            } catch (IOException e) {
                System.err.println("⚠️ Không thể xóa file CV: " + e.getMessage());
            }
        }

        candidateRepository.delete(candidate);
        System.out.println("✅ Candidate deleted successfully");
    }

    // ==================== HELPER METHODS ====================

    /**
     * Lưu file CV vào thư mục uploads/cv/
     */
    private String saveCVFile(MultipartFile file, String candidateName) throws IOException {
        System.out.println("=== HELPER: saveCVFile ===");

        // Tạo thư mục nếu chưa có
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("✅ Đã tạo thư mục: " + uploadDir);
        }

        // Lấy tên file gốc
        String originalFileName = file.getOriginalFilename();
        System.out.println("Original filename: " + originalFileName);

        // Tạo đường dẫn đầy đủ
        Path filePath = Paths.get(uploadDir + originalFileName);

        // Lưu file
        Files.write(filePath, file.getBytes());
        System.out.println("✅ Đã lưu file CV tại: " + filePath.toString());

        return originalFileName;
    }
}