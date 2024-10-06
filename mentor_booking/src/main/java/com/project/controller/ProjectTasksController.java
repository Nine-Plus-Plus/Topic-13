package com.project.controller;

import com.project.dto.ProjectTasksDTO;
import com.project.dto.Response;
import com.project.service.ProjectTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectTasksController {

    @Autowired
    private ProjectTasksService projectTasksService;

    @PostMapping("/admin/create-task")
    public ResponseEntity<Response> createTask(@RequestBody ProjectTasksDTO taskDTO) {
        Response response = projectTasksService.createTask(taskDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/admin/get-all-tasks")
    public ResponseEntity<Response> getAllTasks() {
        Response response = projectTasksService.getAllTasks();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/admin/get-task-by-id/{id}")
    public ResponseEntity<Response> getTaskById(@PathVariable Long id) {
        Response response = projectTasksService.getTaskById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/admin/update-task/{id}")
    public ResponseEntity<Response> updateTask(@PathVariable Long id, @RequestBody ProjectTasksDTO taskDTO) {
        Response response = projectTasksService.updateTask(id, taskDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete-task/{id}")
    public ResponseEntity<Response> deleteTask(@PathVariable Long id) {
        Response response = projectTasksService.deleteTask(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}