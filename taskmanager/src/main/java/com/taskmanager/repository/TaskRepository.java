package com.taskmanager.repository;

import com.taskmanager.entity.Project;
import com.taskmanager.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Tasks,Long> {
    public List<Tasks> findByProject(Project project);
    public List<Tasks> findTop5ByOrderByPriorityDesc();
}

