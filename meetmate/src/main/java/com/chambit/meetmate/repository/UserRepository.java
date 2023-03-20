package com.chambit.meetmate.repository;

import com.chambit.meetmate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findByUserId(String userId);

}
