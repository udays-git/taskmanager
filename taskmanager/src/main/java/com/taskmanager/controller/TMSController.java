package com.taskmanager.controller;

import com.taskmanager.dto.ProjectDTO;
import com.taskmanager.dto.ResponseDTO;
import com.taskmanager.dto.TasksDTO;
import com.taskmanager.service.TMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class TMSController {

    @Autowired
    private TMSService tmsService;

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO>> listProjects(@RequestParam Long userId) {
        return ResponseEntity.ok(tmsService.getProjectsByUserId(userId));
    }

    @PostMapping("/projects")
    public ResponseEntity<ResponseDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(tmsService.createProjectForExistingUser(projectDTO));
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<ResponseDTO> addTask(@PathVariable Long projectId,
                                               @RequestParam Long userId,
                                               @RequestBody TasksDTO task) {
        return ResponseEntity.ok(tmsService.addTaskToProjectForUser(projectId, userId, task));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TasksDTO>> getProjectTasks(@PathVariable Long projectId,
                                                          @RequestParam Long userId) {
        return ResponseEntity.ok(tmsService.fetchTasksForProjectAndUser(projectId, userId));
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<ResponseDTO> deleteProject(@PathVariable Long projectId,
                                                     @RequestParam Long userId) {
        return ResponseEntity.ok(tmsService.deleteProjectAndAssociatedTasksForUser(projectId, userId));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TasksDTO> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(tmsService.fetchTaskById(id));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<ResponseDTO> updateTask(@PathVariable Long id,
                                                  @RequestBody TasksDTO task) {
        return ResponseEntity.ok(tmsService.updateTask(task, id));
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<ResponseDTO> partialUpdateTask(@PathVariable Long id,
                                                         @RequestBody TasksDTO task) {
        return ResponseEntity.ok(tmsService.partialUpdateTask(id, task));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ResponseDTO> deleteTask(@PathVariable Long id) {
        return ResponseEntity.ok(tmsService.deleteTask(id));
    }

    @GetMapping("/tasks/top")
    public ResponseEntity<List<TasksDTO>> getTopTasks(@RequestParam Long userId) {
        return ResponseEntity.ok(tmsService.getTopFivePriorityTasksForUser(userId));
    }
}
