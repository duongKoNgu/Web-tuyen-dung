package com.example.demo.controller;

import com.example.demo.model.Job;
import com.example.demo.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import com.example.demo.model.entity.JobCreate;
import com.example.demo.service.JobService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @PostMapping("/createJob")
    public String createJob( @CookieValue(value = "user_session", required = false) String userIdStr, @RequestBody JobCreate newJob) {

        if (userService.checkIsAdmin(Integer.valueOf(userIdStr))||userService.checkRole(Integer.valueOf(userIdStr)).equals("employer")){
            Integer userId = Integer.parseInt(userIdStr);
            return jobService.createJob(newJob, userId);
        }

        return "Bạn không có quyền tạo Job";
    }

    @GetMapping("/getAllJob")
    public List<Job> getAllJob(HttpServletResponse response) {
        return jobService.getAllJob();
    }

    @GetMapping("/jobs")
    public Page<Job> getJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "jobId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @CookieValue(value = "user_session", defaultValue = "") String userIdStr) {

        // 1. Xử lý Pageable chung (Clean Code: Đưa ra ngoài if/else vì giống hệt nhau)
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 2. Parse User ID
        if (userIdStr == null || userIdStr.isEmpty()) {
            return Page.empty();
        }
        int userId = Integer.parseInt(userIdStr);

        // 3. Kiểm tra Role và gọi Service tương ứng
        if ("admin".equals(userService.checkRole(userId))) {
            return jobService.getJobsWithFilters(search, status, pageable);
        } else {
            return jobService.getMyJobsWithFilters(userId, search, status, pageable);
        }
    }

    @DeleteMapping("/deleteJob/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Integer id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.ok("Xóa Job thành công!");
        } catch (RuntimeException e) {
            // Bắt lỗi RuntimeException (Job not found) từ Service
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi hệ thống khi xóa Job: " + e.getMessage());
        }
    }

    // Trong JobController.java (Cần thay đổi)
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Integer id) { // <-- Thay đổi kiểu trả về
        try {
            Job job = jobService.getJobById(id);
            return ResponseEntity.ok(job);
        } catch (RuntimeException e) {
            // Bắt lỗi Job not found (hoặc lỗi khác từ service)
            return ResponseEntity.status(404).body("Không tìm thấy công việc với ID: " + id); // Trả về 404
        }
    }

    @PutMapping("/updateJob/{id}")
    public ResponseEntity<String> updateJob(
            @PathVariable Integer id,
            @RequestBody JobCreate jobUpdate,
            @CookieValue(value = "user_session", required = false) String userIdStr) {

        // 1. Kiểm tra đăng nhập
        if (userIdStr == null || userIdStr.isEmpty()) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập để thực hiện chức năng này.");
        }

        try {
            Integer userId = Integer.parseInt(userIdStr);

            // 2. Kiểm tra quyền (Admin hoặc Employer)
            if (userService.checkIsAdmin(userId) || "employer".equals(userService.checkRole(userId))) {

                // 3. Gọi Service để update
                // Lưu ý: Service nên kiểm tra thêm xem Employer này có phải chủ sở hữu Job này không
                String result = jobService.updateJob(id, jobUpdate, userId);
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(403).body("Bạn không có quyền sửa Job này.");
            }

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

}