package com.taskmanager;

import com.taskmanager.dto.ProjectDTO;
import com.taskmanager.dto.ResponseDTO;
import com.taskmanager.dto.TasksDTO;
import com.taskmanager.dto.UserDTO;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.Tasks;
import com.taskmanager.entity.User;
import com.taskmanager.exception.TMSException;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.service.Impl.TMSServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskmanagerApplicationTests {

    @InjectMocks
    private TMSServiceImpl tmsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John");
        userDTO.setEmail("john@example.com");

        when(userRepository.existsByName("John")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        ResponseDTO resp = tmsService.registerUser(userDTO);

        assertEquals("User registered successfully", resp.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_NameExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John");
        userDTO.setEmail("john@example.com");

        when(userRepository.existsByName("John")).thenReturn(true);

        TMSException ex = assertThrows(TMSException.class, () -> tmsService.registerUser(userDTO));
        assertEquals("User name already exists", ex.getMessage());
    }

    @Test
    void testCreateProjectForExistingUser_Success() {
        ProjectDTO dto = new ProjectDTO();
        dto.setTitle("ProjA");
        dto.setDescription("Desc");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(3));
        dto.setUserId(10L);

        User user = new User();
        user.setId(10L);
        user.setName("Owner");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        ResponseDTO resp = tmsService.createProjectForExistingUser(dto);

        assertEquals("Project created", resp.getMessage());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testCreateProjectForExistingUser_UserNotFound() {
        ProjectDTO dto = new ProjectDTO();
        dto.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        TMSException ex = assertThrows(TMSException.class, () -> tmsService.createProjectForExistingUser(dto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testAddTaskToProjectForUser_Success() {
        Long projectId = 1L;
        Long userId = 2L;

        User owner = new User();
        owner.setId(userId);

        Project project = new Project();
        project.setId(projectId);
        project.setUser(owner);

        TasksDTO tasksDTO = new TasksDTO();
        tasksDTO.setTitle("Task1");
        tasksDTO.setDescription("Desc1");
        tasksDTO.setStatus("todo");
        tasksDTO.setPriority(4);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ResponseDTO resp = tmsService.addTaskToProjectForUser(projectId, userId, tasksDTO);

        assertEquals("Task added", resp.getMessage());
        verify(taskRepository, times(1)).save(any(Tasks.class));
    }

    @Test
    void testAddTaskToProjectForUser_ProjectNotFound() {
        when(projectRepository.findById(5L)).thenReturn(Optional.empty());

        TMSException ex = assertThrows(TMSException.class, () ->
                tmsService.addTaskToProjectForUser(5L, 1L, new TasksDTO()));
        assertEquals("Project not found", ex.getMessage());
    }

    @Test
    void testAddTaskToProjectForUser_Unauthorized() {
        Long projectId = 3L;
        Long userId = 7L;

        User owner = new User();
        owner.setId(99L); // different owner

        Project project = new Project();
        project.setId(projectId);
        project.setUser(owner);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        TMSException ex = assertThrows(TMSException.class, () ->
                tmsService.addTaskToProjectForUser(projectId, userId, new TasksDTO()));
        assertEquals("Unauthorized", ex.getMessage());
    }

    @Test
    void testFetchTaskById_Success() {
        Tasks task = new Tasks();
        task.setId(1L);
        task.setTitle("Task1");
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TasksDTO dto = tmsService.fetchTaskById(1L);

        assertEquals("Task1", dto.getTitle());
        assertEquals(task.getId(), dto.getId());
    }

    @Test
    void testFetchTaskById_NotFound() {
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        TMSException ex = assertThrows(TMSException.class, () -> tmsService.fetchTaskById(2L));
        assertEquals("Id not found", ex.getMessage());
    }

    @Test
    void testDeleteTask_Success() {
        Tasks task = new Tasks();
        task.setId(10L);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        ResponseDTO resp = tmsService.deleteTask(10L);

        assertEquals("Task deleted", resp.getMessage());
        verify(taskRepository, times(1)).delete(task);
    }
}
