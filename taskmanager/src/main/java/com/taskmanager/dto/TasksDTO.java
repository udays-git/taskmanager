package com.taskmanager.dto;

import com.taskmanager.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class TasksDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectDTO projectDTO;
}
