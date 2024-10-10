package com.project.controller;

import com.project.dto.ProjectsDTO;
import com.project.dto.Response;
import com.project.service.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProjectsController {

    @Autowired
    private ProjectsService projectsService;

    @PostMapping("/admin/create-project")
    public ResponseEntity<Response> createProject(@RequestBody ProjectsDTO createRequest) {
        Response response = projectsService.createProject(createRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/admin/get-all-projects")
    public ResponseEntity<Response> getAllProjects() {
        Response response = projectsService.getAllProjects();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/admin/get-project-by-id/{id}")
    public ResponseEntity<Response> getProjectById(@PathVariable Long id) {
        Response response = projectsService.getProjectById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/admin/update-project/{id}")
    public ResponseEntity<Response> updateProject(@PathVariable Long id, @RequestBody ProjectsDTO updateRequest) {
        Response response = projectsService.updateProject(id, updateRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete-project/{id}")
    public ResponseEntity<Response> deleteProject(@PathVariable Long id) {
        Response response = projectsService.deleteProject(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}