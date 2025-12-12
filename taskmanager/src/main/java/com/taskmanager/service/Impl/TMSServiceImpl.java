package com.taskmanager.service.Impl;

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
import com.taskmanager.service.TMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TMSServiceImpl implements TMSService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    @Transactional
    public ResponseDTO registerUser(UserDTO userDTO) {
        if (userDTO == null || userDTO.getName() == null || userDTO.getEmail() == null) {
            throw new TMSException("Invalid user data");
        }
        if (userRepository.existsByName(userDTO.getName())) {
            throw new TMSException("User name already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new TMSException("Email already registered");
        }
        User user = new User();
        user.setName(userDTO.getName().trim());
        user.setEmail(userDTO.getEmail().trim());
        userRepository.save(user);
        ResponseDTO resp = new ResponseDTO();
        resp.setMessage("User registered successfully");
        return resp;
    }

@Override
public UserDTO loginUser(UserDTO credentials) {
    if (credentials == null || credentials.getName() == null || credentials.getEmail() == null) {
        throw new TMSException("Invalid credentials");
    }

    String name = credentials.getName().trim();
    String email = credentials.getEmail().trim().toLowerCase();

    User user = userRepository.findByNameIgnoreCase(name)
            .orElseThrow(() -> new TMSException("User not found"));

    if (!user.getEmail().trim().equalsIgnoreCase(email)) {
        throw new TMSException("Invalid email for user");
    }

    UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setName(user.getName());
    dto.setEmail(user.getEmail());
    return dto;
}



    @Override
    @Transactional
    public ResponseDTO createProjectForExistingUser(ProjectDTO dto) {
        if (dto == null || dto.getUserId() == null) throw new TMSException("Invalid project data");
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new TMSException("User not found"));
        Project p = new Project();
        p.setTitle(dto.getTitle());
        p.setDescription(dto.getDescription());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        p.setUser(user);
        projectRepository.save(p);
        ResponseDTO resp = new ResponseDTO();
        resp.setMessage("Project created");
        return resp;
    }

    @Override
    public List<ProjectDTO> getProjectsByUserId(Long userId) {
        if (userId == null) throw new TMSException("userId required");
        List<Project> projects = projectRepository.findByUser_Id(userId);
        List<ProjectDTO> dtos = new ArrayList<>();
        for (Project p : projects) {
            ProjectDTO dto = new ProjectDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            dto.setDescription(p.getDescription());
            dto.setStartDate(p.getStartDate());
            dto.setEndDate(p.getEndDate());
            if (p.getUser() != null) dto.setUserId(p.getUser().getId());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    @Transactional
    public ResponseDTO addTaskToProjectForUser(Long projectId, Long userId, TasksDTO tasksDTO) {
        if (projectId == null || userId == null || tasksDTO == null) throw new TMSException("Invalid input");
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new TMSException("Project not found"));
        if (project.getUser() == null || !project.getUser().getId().equals(userId)) throw new TMSException("Unauthorized");
        Tasks t = new Tasks();
        t.setTitle(tasksDTO.getTitle());
        t.setDescription(tasksDTO.getDescription());
        t.setStatus(tasksDTO.getStatus());
        t.setDueDate(tasksDTO.getDueDate());
        t.setPriority(tasksDTO.getPriority());
        t.setProject(project);
        taskRepository.save(t);
        ResponseDTO resp = new ResponseDTO();
        resp.setMessage("Task added");
        return resp;
    }

    @Override
    public List<TasksDTO> fetchTasksForProjectAndUser(Long projectId, Long userId) {
        if (projectId == null || userId == null) throw new TMSException("Invalid input");
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new TMSException("Project not found"));
        if (project.getUser() == null || !project.getUser().getId().equals(userId)) throw new TMSException("Unauthorized");
        List<Tasks> tasks = taskRepository.findByProject(project);
        return tasks.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public TasksDTO fetchTaskById(Long id) {
        Tasks t = taskRepository.findById(id).orElseThrow(() -> new TMSException("Id not found"));
        return mapToDto(t);
    }

    @Override
    @Transactional
    public ResponseDTO updateTask(TasksDTO taskDTO, Long id) {
        Tasks existing = taskRepository.findById(id).orElseThrow(() -> new TMSException("Id not found"));
        if (taskDTO.getPriority() == null) throw new TMSException("Priority must be provided by the user");
        if (taskDTO.getPriority() < 1 || taskDTO.getPriority() > 10) throw new TMSException("Priority must be between 1 and 10");
        existing.setTitle(taskDTO.getTitle());
        existing.setDescription(taskDTO.getDescription());
        existing.setStatus(taskDTO.getStatus());
        existing.setDueDate(taskDTO.getDueDate());
        existing.setPriority(taskDTO.getPriority());
        existing.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(existing);
        ResponseDTO resp = new ResponseDTO();
        resp.setMessage("Task updated successfully");
        return resp;
    }

    @Override
    @Transactional
    public ResponseDTO partialUpdateTask(Long id, TasksDTO partial) {
        Tasks existing = taskRepository.findById(id).orElseThrow(() -> new TMSException("Id not found"));
        if (partial.getStatus() != null) existing.setStatus(partial.getStatus());
        if (partial.getTitle() != null) existing.setTitle(partial.getTitle());
        if (partial.getDescription() != null) existing.setDescription(partial.getDescription());
        if (partial.getPriority() != null) {
            if (partial.getPriority() < 1 || partial.getPriority() > 10) throw new TMSException("Priority must be between 1 and 10");
            existing.setPriority(partial.getPriority());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(existing);
        ResponseDTO resp = new ResponseDTO();
        resp.setMessage("Task partially updated");
        return resp;
    }

    @Override
    @Transactional
    public ResponseDTO deleteTask(Long id) {
        Tasks t = taskRepository.findById(id).orElseThrow(() -> new TMSException("Id not found"));
        taskRepository.delete(t);
        ResponseDTO resp = new ResponseDTO();
        resp.setMessage("Task deleted");
        return resp;
    }

    @Override
    @Transactional
    public ResponseDTO deleteProjectAndAssociatedTasksForUser(Long projectId, Long userId) {
        if (projectId == null || userId == null) throw new TMSException("Invalid input");
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new TMSException("Project not found"));
        if (project.getUser() == null || !project.getUser().getId().equals(userId)) throw new TMSException("Unauthorized");
        projectRepository.delete(project);
        ResponseDTO resp = new ResponseDTO();
        resp.setMessage("Project deleted");
        return resp;
    }

@Override
public List<TasksDTO> getTopFivePriorityTasksForUser(Long userId) {
    if (userId == null) throw new TMSException("userId required");
    List<Tasks> tasks = taskRepository.findByProject_User_Id(userId);
    return tasks.stream()
            .sorted((a, b) -> {
                int pa = a.getPriority() == null ? 0 : a.getPriority();
                int pb = b.getPriority() == null ? 0 : b.getPriority();
                return Integer.compare(pb, pa);
            })
            .limit(5)
            .map(this::mapToDto)
            .collect(Collectors.toList());
}


    private TasksDTO mapToDto(Tasks t) {
        if (t == null) return null;
        TasksDTO dto = new TasksDTO();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setStatus(t.getStatus());
        dto.setDueDate(t.getDueDate());
        dto.setPriority(t.getPriority());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());
        if (t.getProject() != null) {
            ProjectDTO p = new ProjectDTO();
            p.setId(t.getProject().getId());
            p.setTitle(t.getProject().getTitle());
            p.setDescription(t.getProject().getDescription());
            p.setStartDate(t.getProject().getStartDate());
            p.setEndDate(t.getProject().getEndDate());
            if (t.getProject().getUser() != null) p.setUserId(t.getProject().getUser().getId());
            dto.setProjectDTO(p);
        }
        return dto;
    }
}
