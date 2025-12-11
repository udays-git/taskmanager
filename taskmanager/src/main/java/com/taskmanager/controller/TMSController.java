package com.taskmanager.controller;


import com.taskmanager.dto.ResponseDTO;
import com.taskmanager.dto.TasksDTO;
import com.taskmanager.dto.UserDTO;
import com.taskmanager.exception.TMSException;
import com.taskmanager.service.TMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/TMS")
@CrossOrigin
public class TMSController {

    @Autowired
    public TMSService tmsService;

    @PostMapping(value = "add-user-and-projects")
    public ResponseEntity<ResponseDTO> addUserAndProjects(@RequestBody UserDTO userDTO) throws TMSException {
        ResponseDTO responseDTO = tmsService.addUserAndProjects(userDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping(value = "/add-tasks/{title}")
    public ResponseEntity<ResponseDTO> addTasks(@PathVariable  String title, @RequestBody TasksDTO tasksDTO) throws TMSException {
        ResponseDTO responseDTO = tmsService.addTasksForProject(title,tasksDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping(value ="/fetchProjectDetailsAndTasks/{title}" )
    public ResponseEntity<List<TasksDTO>> fetchProjectDetailsAndTasks(@PathVariable  String title) throws TMSException {

        List<TasksDTO> tasksdto = tmsService.fetchProjectsAndTasks(title);
        return new ResponseEntity<>(tasksdto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete-project/{title}")
    public ResponseEntity<ResponseDTO> deleteProjectAndAssociatedTasks(@PathVariable String title) throws TMSException {
        ResponseDTO responseDTO = tmsService.deleteProjectAndAssociatedTasks(title);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteTask(@PathVariable Long id) {
        ResponseDTO responseDto = tmsService.deleteTask(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TasksDTO> fetchTaskById(@PathVariable Long id) {
        TasksDTO taskDto = tmsService.fetchTaskById(id);
        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateTask(@RequestBody TasksDTO taskDto, @PathVariable Long id) {
        ResponseDTO responseDto = tmsService.updateTask(taskDto, id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO> partialUpdateTask(@PathVariable Long id, @RequestBody TasksDTO partial) {
        ResponseDTO responseDto = tmsService.partialUpdateTask(id, partial);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/top")
    public ResponseEntity<List<TasksDTO>> getTopFive() {
        return ResponseEntity.ok(tmsService.getTopFivePriorityTasks());
    }






}
