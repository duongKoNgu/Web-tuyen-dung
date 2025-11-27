package com.example.demo.service;

import com.example.demo.model.User;

public interface UserService {
    public User login(String userName,String password);
    public String createUser(User user);
    public Boolean checkIsAdmin(Integer id);
    public String checkRole(Integer id);

}
