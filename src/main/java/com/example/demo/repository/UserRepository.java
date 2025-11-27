package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.userName = :username")
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.idUser = :userId")
    User findByUserId(Integer userId);

}