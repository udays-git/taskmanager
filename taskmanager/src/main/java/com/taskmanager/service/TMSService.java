package com.taskmanager.service;

import com.taskmanager.dto.ResponseDTO;
import com.taskmanager.dto.TasksDTO;
import com.taskmanager.dto.UserDTO;
import com.taskmanager.exception.TMSException;

import java.util.List;

public interface TMSService {
    public ResponseDTO addUserAndProjects(UserDTO userDTO) throws TMSException;
    public ResponseDTO addTasksForProject(String title, TasksDTO tasksDTO) throws TMSException;
    public List<TasksDTO> fetchProjectsAndTasks(String title) throws TMSException;
    public ResponseDTO deleteProjectAndAssociatedTasks(String title) throws TMSException;
    public TasksDTO fetchTaskById(Long id) throws TMSException ;
    //List<TaskDto> fetchAllTasks();
    ResponseDTO updateTask(TasksDTO taskDTO,Long id);
    ResponseDTO deleteTask(Long id);
    List<TasksDTO> getTopFivePriorityTasks();
    ResponseDTO partialUpdateTask(Long id, TasksDTO partial);
}
