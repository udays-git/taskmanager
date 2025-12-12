package com.taskmanager.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TasksDTO {
    private Long id;

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;
    private String status;
    private LocalDate dueDate;

    @Min(value = 1, message = "Priority must be >= 1")
    @Max(value = 10, message = "Priority must be <= 10")
    private Integer priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectDTO projectDTO;
}
