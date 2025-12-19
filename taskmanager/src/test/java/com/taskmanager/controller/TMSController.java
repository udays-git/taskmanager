package com.taskmanager.controller;

import com.taskmanager.dto.ProjectDTO;
import com.taskmanager.dto.ResponseDTO;
import com.taskmanager.dto.TasksDTO;
import com.taskmanager.service.TMSService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TMSControllerTest {

    @Mock
    private TMSService tmsService;

    @InjectMocks
    private TMSController controller;

    @Test
    void listProjects_success() {
        ProjectDTO project = new ProjectDTO();
        project.setTitle("TMS");

        when(tmsService.getProjectsByUserId(1L))
                .thenReturn(List.of(project));

        ResponseEntity<List<ProjectDTO>> response =
                controller.listProjects(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("TMS", response.getBody().get(0).getTitle());

        verify(tmsService).getProjectsByUserId(1L);
    }

    @Test
    void createProject_success() {
        ProjectDTO dto = new ProjectDTO();
        dto.setTitle("Project");

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Project created");

        when(tmsService.createProjectForExistingUser(dto))
                .thenReturn(responseDTO);

        ResponseEntity<ResponseDTO> response =
                controller.createProject(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Project created", response.getBody().getMessage());

        verify(tmsService).createProjectForExistingUser(dto);
    }

    @Test
    void deleteProject_success() {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Project deleted");

        when(tmsService.deleteProjectAndAssociatedTasksForUser(10L, 1L))
                .thenReturn(responseDTO);

        ResponseEntity<ResponseDTO> response =
                controller.deleteProject(10L, 1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Project deleted", response.getBody().getMessage());
    }

    @Test
    void addTask_success() {
        TasksDTO task = new TasksDTO();
        task.setTitle("Task 1");

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Task added");

        when(tmsService.addTaskToProjectForUser(10L, 1L, task))
                .thenReturn(responseDTO);

        ResponseEntity<ResponseDTO> response =
                controller.addTask(10L, 1L, task);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Task added", response.getBody().getMessage());
    }

    @Test
    void getProjectTasks_success() {
        TasksDTO task = new TasksDTO();
        task.setTitle("Task");

        when(tmsService.fetchTasksForProjectAndUser(10L, 1L))
                .thenReturn(List.of(task));

        ResponseEntity<List<TasksDTO>> response =
                controller.getProjectTasks(10L, 1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getTask_success() {
        TasksDTO task = new TasksDTO();
        task.setId(100L);

        when(tmsService.fetchTaskById(100L))
                .thenReturn(task);

        ResponseEntity<TasksDTO> response =
                controller.getTask(100L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(100L, response.getBody().getId());
    }

    @Test
    void updateTask_success() {
        TasksDTO task = new TasksDTO();
        task.setPriority(7);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Task updated successfully");

        when(tmsService.updateTask(task, 100L))
                .thenReturn(responseDTO);

        ResponseEntity<ResponseDTO> response =
                controller.updateTask(100L, task);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Task updated successfully",
                response.getBody().getMessage());
    }

    @Test
    void partialUpdateTask_success() {
        TasksDTO task = new TasksDTO();
        task.setStatus("DONE");

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Task partially updated");

        when(tmsService.partialUpdateTask(100L, task))
                .thenReturn(responseDTO);

        ResponseEntity<ResponseDTO> response =
                controller.partialUpdateTask(100L, task);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Task partially updated",
                response.getBody().getMessage());
    }

    @Test
    void deleteTask_success() {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Task deleted");

        when(tmsService.deleteTask(100L))
                .thenReturn(responseDTO);

        ResponseEntity<ResponseDTO> response =
                controller.deleteTask(100L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Task deleted", response.getBody().getMessage());
    }

    @Test
    void getTopTasks_success() {
        TasksDTO task = new TasksDTO();
        task.setPriority(10);

        when(tmsService.getTopFivePriorityTasksForUser(1L))
                .thenReturn(List.of(task));

        ResponseEntity<List<TasksDTO>> response =
                controller.getTopTasks(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }
}
