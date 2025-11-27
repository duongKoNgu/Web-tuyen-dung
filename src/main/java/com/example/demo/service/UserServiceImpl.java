package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository repository;


    @Override
    public User login(String userName, String password) {
        User user = repository.findByUsername(userName);

        if (user != null && password != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public String createUser(User u) {
        User user = repository.findByUsername(u.getUserName());

        if(user != null){
            return "Username đã tồn tại";
        }else {
            User createUser = new User();
            createUser.setUserName(u.getUserName());
            createUser.setEmail(u.getEmail());
            createUser.setRole(u.getRole());
            createUser.setPassword(u.getPassword());

            repository.save(createUser);
        }

        return "tạo tài khoản thành công";
    }

    @Override
    public Boolean checkIsAdmin(Integer id) {
        User user = repository.findByUserId(id);
        if (user.getRole().equals("Admin")){
            return Boolean.TRUE;
        }

        return false;
    }

    @Override
    public String checkRole(Integer id) {
        User user = repository.findByUserId(id);
        return user.getRole();
    }


}
