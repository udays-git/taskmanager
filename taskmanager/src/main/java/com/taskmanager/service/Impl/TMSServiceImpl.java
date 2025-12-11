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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TMSServiceImpl implements TMSService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public ResponseDTO addUserAndProjects(UserDTO userDTO) throws TMSException {
        User user=userRepository.findByName(userDTO.getName());
        if(user!=null){
            throw new TMSException("User Already Exists");
        }
        user=new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        List<Project> ListOfProjects =new ArrayList<>();
        for(ProjectDTO projectDTO : userDTO.getProjectDTO()){
            Project project = new Project();
            project.setTitle(projectDTO.getTitle());
            project.setDescription(projectDTO.getDescription());
            project.setStartDate(projectDTO.getStartDate());
            project.setEndDate(projectDTO.getEndDate());
            ListOfProjects.add(project);
        }
        user.setProject(ListOfProjects);
        userRepository.save(user);
        ResponseDTO responseDTO=new ResponseDTO();
        responseDTO.setMessage("User Added Successfully with associated user projects");
        return responseDTO;

    }

    @Override
    public ResponseDTO addTasksForProject(String title, TasksDTO tasksDTO) throws TMSException {
        Project project = projectRepository.findByTitle(title);

        if (project == null) {
            throw new TMSException("Project not found");
        }
        Tasks tasks = new Tasks();
        tasks.setTitle(tasksDTO.getTitle());
        tasks.setDescription(tasksDTO.getDescription());
        tasks.setStatus(tasksDTO.getStatus());
        tasks.setDueDate(tasksDTO.getDueDate());
        tasks.setPriority(tasksDTO.getPriority());

        tasks.setProject(project);

        taskRepository.save(tasks);

        ResponseDTO responseDTO=new ResponseDTO();
        responseDTO.setMessage("Tasks Added For A Project");
        return responseDTO;

    }

    @Override
    public List<TasksDTO> fetchProjectsAndTasks(String title) throws TMSException {
        Project project = projectRepository.findByTitle(title);

        if (project == null) {
            throw new TMSException("Project Not Found");
        }

        List<Tasks> tasksList = taskRepository.findByProject(project);
        List<TasksDTO > tasksDTOList = new ArrayList<>();

        for(Tasks tasks : tasksList){

            TasksDTO tasksDTO = new TasksDTO();

            tasksDTO.setId(tasks.getId());
            tasksDTO.setTitle(tasks.getTitle());
            tasksDTO.setDescription(tasks.getDescription());
            tasksDTO.setStatus(tasks.getStatus());
            tasksDTO.setDueDate(tasks.getDueDate());
            tasksDTO.setPriority(tasks.getPriority());

            ProjectDTO projectDTO = new ProjectDTO();

            projectDTO.setId(tasks.getProject().getId());
            projectDTO.setTitle(tasks.getProject().getTitle());
            projectDTO.setDescription(tasks.getProject().getDescription());
            projectDTO.setStartDate(tasks.getProject().getStartDate());
            projectDTO.setEndDate(tasks.getProject().getEndDate());
            tasksDTO.setProjectDTO(projectDTO);
            tasksDTOList.add(tasksDTO);
        }
        return tasksDTOList;


    }

    @Override
    public ResponseDTO deleteProjectAndAssociatedTasks(String title) throws TMSException {
        Project project = projectRepository.findByTitle(title);

        if (project == null) {
            throw new TMSException("Project not found");
        }

        taskRepository.deleteAll( taskRepository.findByProject(project));;
        projectRepository.delete(project);

        ResponseDTO responseDTO=new ResponseDTO();
        responseDTO.setMessage("Deleted  Successfully ");
        return responseDTO;

    }
    public TasksDTO mapToDto(Tasks t) {
        TasksDTO dto = new TasksDTO();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setStatus(t.getStatus());
        dto.setDueDate(t.getDueDate());
        dto.setPriority(t.getPriority());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());
        return dto;
    }

    @Override
    public TasksDTO fetchTaskById(Long id) throws TMSException {
        Tasks t = taskRepository.findById(id)
                .orElseThrow(() -> new TMSException("Id not found"));
        TasksDTO taskDto = mapToDto(t);
        return taskDto;
    }

    @Override
    public ResponseDTO updateTask(TasksDTO taskDTO, Long id) {
        Tasks existing = taskRepository.findById(id)
                .orElseThrow(() -> new TMSException("Id not found"));

        if (taskDTO.getPriority() == null) {
            throw new TMSException("Priority must be provided by the user");
        }
        if (taskDTO.getPriority() < 1 || taskDTO.getPriority() > 10) {
            throw new TMSException("Priority must be between 1 and 10");
        }

        existing.setTitle(taskDTO.getTitle());
        existing.setDescription(taskDTO.getDescription());
        existing.setStatus(taskDTO.getStatus());
        existing.setDueDate(taskDTO.getDueDate());
        existing.setPriority(taskDTO.getPriority());
        existing.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(existing);

        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage("Task Updated successfully");
        return responseDto;
    }

    @Override
    public ResponseDTO deleteTask(Long id) {
        Tasks t = taskRepository.findById(id)
                .orElseThrow(() -> new TMSException("Id not found"));
        taskRepository.delete(t);
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage("Task is deleted");
        return responseDto;
    }

    @Override
    public List<TasksDTO> getTopFivePriorityTasks() {
        List<Tasks> tasks = taskRepository.findTop5ByOrderByPriorityDesc();
        List<TasksDTO> dtoList = new ArrayList<>();

        for (Tasks t : tasks) {
            dtoList.add(mapToDto(t));
        }
        return dtoList;
    }

    @Override
    public ResponseDTO partialUpdateTask(Long id, TasksDTO partial) {
        Tasks existing = taskRepository.findById(id)
                .orElseThrow(() -> new TMSException("Id not found"));

        if (partial.getStatus() != null) existing.setStatus(partial.getStatus());
        if (partial.getTitle() != null) existing.setTitle(partial.getTitle());
        existing.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(existing);
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage("Task is deleted");
        return responseDto;

    }
}
