package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/userLogin")
    public User getUserLogin(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             HttpServletResponse response) {

        System.out.println("=== LOGIN REQUEST ===");
        System.out.println("Username: " + username);

        User user = userService.login(username, password);

        if (user != null) {
            System.out.println("Login success! User ID: " + user.getIdUser());


            response.setHeader("Set-Cookie",
                    String.format("user_session=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax",
                            user.getIdUser().toString(), 24 * 60 * 60));

            System.out.println("Cookie set!");
            return user;
        }

        System.out.println("Login failed!");
        return null;
    }

    @PostMapping("/createUser")
    public String createUser(@RequestBody User newUser) {
        return userService.createUser(newUser);
    }
}