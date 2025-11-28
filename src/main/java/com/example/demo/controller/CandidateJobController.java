package com.example.demo.controller;

import com.example.demo.service.CandidateJobServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/apply")
public class CandidateJobController {

    @Autowired
    private CandidateJobServiceImpl candidateJobServiceImpl;

    @PostMapping("/{jobId}")
    public ResponseEntity<String> applyForJob(
            @PathVariable Integer jobId,
            @CookieValue(value = "user_session", required = false) String candidateIdStr) {

        // 1. Kiểm tra xem Cookie có tồn tại không (Người dùng đã đăng nhập chưa?)
        if (candidateIdStr == null || candidateIdStr.isEmpty()) {
            return ResponseEntity.status(401).body("Lỗi: Vui lòng đăng nhập để ứng tuyển.");
        }

        Integer userId;
        try {
            // Chuyển đổi ID từ String sang Integer
            userId = Integer.parseInt(candidateIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("Lỗi: Phiên đăng nhập không hợp lệ.");
        }

        try {

            String result = candidateJobServiceImpl.applyJob(userId, jobId);

            if (result.contains("thành công")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result); // Đã ứng tuyển
            }
        } catch (RuntimeException e) {

            return ResponseEntity.status(404).body("Lỗi ứng tuyển: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống khi ứng tuyển.");
        }
    }
}