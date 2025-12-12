package com.taskmanager.repository;

import com.taskmanager.entity.Tasks;
import com.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Tasks, Long> {

    List<Tasks> findByProject(Project project);

    List<Tasks> findByProject_User_Id(Long userId);

    List<Tasks> findTop5ByProject_User_IdOrderByPriorityDesc(Long userId);
}
