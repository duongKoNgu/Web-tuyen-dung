package com.example.demo.controller;

import com.example.demo.model.Candidate;
import com.example.demo.model.entity.CandidateMto;
import com.example.demo.service.CandidateService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController

@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private UserService userService;


    private final String uploadDir = "uploads/cv/";


    /**
     * Lấy thông tin candidate profile của user đang đăng nhập (qua cookie)
     * Endpoint: GET /candidate/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getCandidateProfile(
            @CookieValue(value = "user_session", required = false) String userIdStr) {

        System.out.println("=== GET CANDIDATE PROFILE ===");
        System.out.println("Cookie userId: " + userIdStr);

        // Kiểm tra cookie
        if (userIdStr == null || userIdStr.isEmpty()) {
            System.out.println("❌ Cookie không tồn tại");
            return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
        }

        try {
            Integer userId = Integer.valueOf(userIdStr);
            System.out.println("✅ Tìm candidate với userId: " + userId);

            CandidateMto candidate = candidateService.getCandidateByUserId(userId);

            if (candidate == null) {
                System.out.println("ℹ️ Chưa có thông tin candidate cho userId: " + userId);
                return ResponseEntity.ok().body(null);
            }

            return ResponseEntity.ok(candidate);

        } catch (NumberFormatException e) {
            System.out.println("❌ Cookie không hợp lệ: " + userIdStr);
            return ResponseEntity.badRequest().body("Cookie không hợp lệ");
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    /**
     * Thêm candidate mới (tự động lấy userId từ cookie)
     * Endpoint: POST /candidate/add
     */
    @PostMapping("/add")
    public ResponseEntity<String> addCandidate(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("gender") String gender,
            @RequestParam("address") String address,
            @RequestParam("status") String status,
            @RequestParam("bday") String bday,
            @RequestParam(value = "cv", required = false) MultipartFile cvFile,
            @CookieValue(value = "user_session", required = false) String userIdStr) {

        System.out.println("=== ADD CANDIDATE ===");
        System.out.println("Cookie userId: " + userIdStr);

        try {
            // Kiểm tra đăng nhập
            if (userIdStr == null || userIdStr.isEmpty()) {
                return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
            }

            Integer userId = Integer.valueOf(userIdStr);
            System.out.println("✅ Tạo candidate cho userId: " + userId);

            Candidate candidate = new Candidate();
            candidate.setName(name);
            candidate.setEmail(email);
            candidate.setPhone(phone);
            candidate.setGender(gender);
            candidate.setAddress(address);
            candidate.setStatus(status);
            candidate.setBday(bday);

            Candidate savedCandidate = candidateService.saveCandidate(candidate, cvFile, userId);

            System.out.println("✅ Thêm candidate thành công! ID: " + savedCandidate.getCandidateId());
            return ResponseEntity.ok("Thêm ứng viên thành công! ID: " + savedCandidate.getCandidateId());

        } catch (NumberFormatException e) {
            System.out.println("❌ Cookie không hợp lệ");
            return ResponseEntity.badRequest().body("Cookie không hợp lệ");
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Cập nhật thông tin candidate
     * Endpoint: PUT /candidate/updateCandidate/{id}
     */
    @PutMapping("/updateCandidate/{id}")
    public ResponseEntity<String> updateCandidate(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("gender") String gender,
            @RequestParam("address") String address,
            @RequestParam("status") String status,
            @RequestParam("bday") String bday,
            @RequestParam(value = "cv", required = false) MultipartFile cvFile,
            @CookieValue(value = "user_session", required = false) String userIdStr) {

        System.out.println("=== UPDATE CANDIDATE ===");
        System.out.println("Candidate ID: " + id);
        System.out.println("Cookie userId: " + userIdStr);

        try {
            // Kiểm tra đăng nhập
            if (userIdStr == null || userIdStr.isEmpty()) {
                return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
            }

            Candidate candidate = new Candidate();
            candidate.setName(name);
            candidate.setEmail(email);
            candidate.setPhone(phone);
            candidate.setGender(gender);
            candidate.setAddress(address);
            candidate.setStatus(status);
            candidate.setBday(bday);

            candidateService.updateCandidate(id, candidate, cvFile);

            System.out.println("✅ Cập nhật candidate thành công!");
            return ResponseEntity.ok("Cập nhật thành công!");

        } catch (RuntimeException e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Lỗi hệ thống: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // ==================== READ (Query) ====================

    /**
     * Lấy tất cả candidates (không phân trang)
     * Endpoint: GET /candidate/all
     */
    @GetMapping("/all")
    public List<Candidate> getAllCandidates() {
        return candidateService.getCandidates();
    }

    /**
     * Lấy candidates với phân trang và filter
     * Endpoint: GET /candidate/candidates
     */
    @GetMapping("/candidates")
    public ResponseEntity<Page<Object[]>> getCandidates( // <--- Đổi thành Page<Object[]>
                                                         @RequestParam(required = false) String search,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "candidateId") String sortBy,
                                                         @RequestParam(defaultValue = "desc") String sortDir,
                                                         @CookieValue(value = "user_session", defaultValue = "") String userIdStr
    ) {
        if (userIdStr == null || userIdStr.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        int userId = Integer.parseInt(userIdStr);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if ("Admin".equals(userService.checkRole(userId))) {
            return ResponseEntity.ok(candidateService.getCandidatesWithFilters(search, status, pageable));
        } else {
            return ResponseEntity.ok(candidateService.getMyCandidatesWithFilters(userId, search, status, pageable));
        }
    }

    /**
     * Lấy candidate theo userId (cho admin hoặc public API)
     * Endpoint: GET /candidate/{userId}
     */
    @GetMapping("/userId")
    public ResponseEntity<?> getCandidateByUserId(
            @RequestParam("userId") Integer userId,
            @CookieValue(value = "user_session", defaultValue = "") String userIdStr) {

        if (userId == null || userIdStr.isEmpty()) {
            return ResponseEntity.badRequest().body("Thiếu thông tin xác thực");
        }

        if (String.valueOf(userId).equals(userIdStr)|| userService.checkIsAdmin(Integer.valueOf(userIdStr))) {

            CandidateMto candidate = candidateService.getCandidateByUserId(userId);

            if (candidate == null) {
                System.out.println("ℹ️ Không tìm thấy candidate cho userId: " + userId);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(candidate);
        } else {
            // Nếu ID yêu cầu khác ID đang đăng nhập -> Chặn truy cập
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền xem hồ sơ này");
        }
    }

    // ==================== CV FILE HANDLING ====================

    /**
     * Download/View CV file
     * Endpoint: GET /candidate/cv/{filename}
     */
    @GetMapping("/cv/{filename:.+}")
    public ResponseEntity<Resource> getCV(@PathVariable String filename) {
        System.out.println("=== GET CV FILE ===");
        System.out.println("Filename: " + filename);

        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                System.out.println("✅ File tồn tại: " + filename);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/pdf"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                System.out.println("❌ File không tồn tại: " + filename);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi load file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ==================== DELETE ====================

    /**
     * Xóa candidate
     * Endpoint: DELETE /candidate/deleteCandidate/{id}
     */
    @DeleteMapping("/deleteCandidate/{id}")
    public ResponseEntity<String> deleteCandidate(
            @PathVariable Integer id,
            @CookieValue(value = "user_session", required = false) String userIdStr) {

        System.out.println("=== DELETE CANDIDATE ===");
        System.out.println("Candidate ID: " + id);


        try {
            // Kiểm tra đăng nhập
            if (userIdStr == null || userIdStr.isEmpty()) {
                return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
            }

            candidateService.deleteCandidate(id);

            System.out.println("✅ Xóa candidate thành công!");
            return ResponseEntity.ok("Xóa candidate thành công!");

        } catch (RuntimeException e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Lỗi hệ thống: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}