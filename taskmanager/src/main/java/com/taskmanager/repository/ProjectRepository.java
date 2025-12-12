package com.taskmanager.repository;

import com.taskmanager.entity.Project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByTitle(String title);
    List<Project> findByUser_Id(Long userId);

}
