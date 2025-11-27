package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/userLogin")
    public ResponseEntity<?> getUserLogin(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {

        System.out.println("=== LOGIN REQUEST ===");

        User user = userService.login(username, password);

        if (user != null) {
            System.out.println("Login success! User ID: " + user.getIdUser());

            // --- CẤU HÌNH COOKIE CHUẨN ---
            ResponseCookie cookie = ResponseCookie.from("user_session", user.getIdUser().toString())
                    .httpOnly(true)       // JS không đọc được (Bảo mật)
                    .secure(true)         // Bắt buộc TRUE để dùng được SameSite=None
                    .path("/")
                    .maxAge(24 * 60 * 60) // 1 ngày
                    .sameSite("None")     // QUAN TRỌNG: Cho phép gửi chéo domain/port
                    .build();

            // Trả về User kèm Header Set-Cookie
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(user);
        }

        return ResponseEntity.status(401).body("Login failed!");
    }

    @PostMapping("/createUser")
    public String createUser(@RequestBody User newUser) {
        return userService.createUser(newUser);
    }
}