package com.taskmanager.service.Impl;
import com.taskmanager.dto.*;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.Tasks;
import com.taskmanager.entity.User;
import com.taskmanager.exception.TMSException;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TMSServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TMSServiceImpl service;

    private User user;
    private Project project;
    private Tasks task;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setName("Uday");
        user.setEmail("uday@test.com");

        project = new Project();
        project.setId(10L);
        project.setTitle("TMS");
        project.setUser(user);

        task = new Tasks();
        task.setId(100L);
        task.setTitle("Task 1");
        task.setPriority(8);
        task.setProject(project);
    }
    @Test
    void registerUser_success() {
        UserDTO dto = new UserDTO();
        dto.setName("Uday");
        dto.setEmail("uday@test.com");

        when(userRepository.existsByName("Uday")).thenReturn(false);
        when(userRepository.existsByEmail("uday@test.com")).thenReturn(false);

        ResponseDTO response = service.registerUser(dto);

        assertEquals("User registered successfully", response.getMessage());
        verify(userRepository).save(any(User.class));
    }
    @Test
    void registerUser_duplicateEmail() {
        UserDTO dto = new UserDTO();
        dto.setName("Uday");
        dto.setEmail("uday@test.com");

        when(userRepository.existsByName("Uday")).thenReturn(false);
        when(userRepository.existsByEmail("uday@test.com")).thenReturn(true);

        assertThrows(TMSException.class, () -> service.registerUser(dto));
    }
    @Test
    void loginUser_success() {
        UserDTO dto = new UserDTO();
        dto.setName("Uday");
        dto.setEmail("uday@test.com");

        when(userRepository.findByNameIgnoreCase("Uday"))
                .thenReturn(Optional.of(user));

        UserDTO result = service.loginUser(dto);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void loginUser_invalidEmail() {
        UserDTO dto = new UserDTO();
        dto.setName("Uday");
        dto.setEmail("wrong@test.com");

        when(userRepository.findByNameIgnoreCase("Uday"))
                .thenReturn(Optional.of(user));

        assertThrows(TMSException.class, () -> service.loginUser(dto));
    }
    @Test
    void createProject_success() {
        ProjectDTO dto = new ProjectDTO();
        dto.setUserId(1L);
        dto.setTitle("Project");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseDTO response = service.createProjectForExistingUser(dto);

        assertEquals("Project created", response.getMessage());
        verify(projectRepository).save(any(Project.class));
    }
    @Test
    void getProjectsByUserId_success() {
        when(projectRepository.findByUser_Id(1L))
                .thenReturn(List.of(project));

        List<ProjectDTO> result = service.getProjectsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(project.getTitle(), result.get(0).getTitle());
    }
    @Test
    void addTaskToProject_success() {
        TasksDTO dto = new TasksDTO();
        dto.setTitle("Task");
        dto.setPriority(5);

        when(projectRepository.findById(10L))
                .thenReturn(Optional.of(project));

        ResponseDTO response = service.addTaskToProjectForUser(10L, 1L, dto);

        assertEquals("Task added", response.getMessage());
        verify(taskRepository).save(any(Tasks.class));
    }
    @Test
    void fetchTasksForProjectAndUser_success() {
        when(projectRepository.findById(10L))
                .thenReturn(Optional.of(project));
        when(taskRepository.findByProject(project))
                .thenReturn(List.of(task));

        List<TasksDTO> result =
                service.fetchTasksForProjectAndUser(10L, 1L);

        assertEquals(1, result.size());
    }
    @Test
    void fetchTaskById_success() {
        when(taskRepository.findById(100L))
                .thenReturn(Optional.of(task));

        TasksDTO dto = service.fetchTaskById(100L);

        assertEquals(task.getId(), dto.getId());
    }
    @Test
    void updateTask_success() {
        TasksDTO dto = new TasksDTO();
        dto.setPriority(7);

        when(taskRepository.findById(100L))
                .thenReturn(Optional.of(task));

        ResponseDTO response = service.updateTask(dto, 100L);

        assertEquals("Task updated successfully", response.getMessage());
        verify(taskRepository).save(task);
    }
    @Test
    void partialUpdateTask_success() {
        TasksDTO dto = new TasksDTO();
        dto.setStatus("DONE");

        when(taskRepository.findById(100L))
                .thenReturn(Optional.of(task));

        ResponseDTO response = service.partialUpdateTask(100L, dto);

        assertEquals("Task partially updated", response.getMessage());
    }
    @Test
    void deleteTask_success() {
        when(taskRepository.findById(100L))
                .thenReturn(Optional.of(task));

        ResponseDTO response = service.deleteTask(100L);

        assertEquals("Task deleted", response.getMessage());
        verify(taskRepository).delete(task);
    }
    @Test
    void deleteProject_success() {
        when(projectRepository.findById(10L))
                .thenReturn(Optional.of(project));

        ResponseDTO response =
                service.deleteProjectAndAssociatedTasksForUser(10L, 1L);

        assertEquals("Project deleted", response.getMessage());
        verify(projectRepository).delete(project);
    }
    @Test
    void getTopFivePriorityTasks_success() {
        when(taskRepository.findByProject_User_Id(1L))
                .thenReturn(List.of(task));

        List<TasksDTO> result = service.getTopFivePriorityTasksForUser(1L);

        assertEquals(1, result.size());
    }
}
