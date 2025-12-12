package com.taskmanager.service;

import java.util.List;

import com.taskmanager.dto.ProjectDTO;
import com.taskmanager.dto.ResponseDTO;
import com.taskmanager.dto.TasksDTO;
import com.taskmanager.dto.UserDTO;

public interface TMSService {

    ResponseDTO registerUser(UserDTO userDTO);
    UserDTO loginUser(UserDTO credentials);

    ResponseDTO createProjectForExistingUser(ProjectDTO dto);
    List<ProjectDTO> getProjectsByUserId(Long userId);
    ResponseDTO deleteProjectAndAssociatedTasksForUser(Long projectId, Long userId);

    ResponseDTO addTaskToProjectForUser(Long projectId, Long userId, TasksDTO taskDTO);
    List<TasksDTO> fetchTasksForProjectAndUser(Long projectId, Long userId);
    TasksDTO fetchTaskById(Long id);
    ResponseDTO updateTask(TasksDTO taskDTO, Long id);
    ResponseDTO partialUpdateTask(Long id, TasksDTO partial);
    ResponseDTO deleteTask(Long id);

    List<TasksDTO> getTopFivePriorityTasksForUser(Long userId);

}
