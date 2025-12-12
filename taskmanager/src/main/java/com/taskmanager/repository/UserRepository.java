package com.taskmanager.repository;

import com.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);               
    boolean existsByName(String name);
    Optional<User> findByEmail(String email);    
    boolean existsByEmail(String email);
        Optional<User> findByNameIgnoreCase(String name);

}
