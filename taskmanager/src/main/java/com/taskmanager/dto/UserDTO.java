package com.taskmanager.dto;


import com.taskmanager.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private List<ProjectDTO> projectDTO ;
}
