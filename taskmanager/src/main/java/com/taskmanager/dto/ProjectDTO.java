package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull(message = "userId (owner) is required")
    private Long userId;
}
