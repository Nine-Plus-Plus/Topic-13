package com.project.service;

import com.project.dto.ClassDTO;
import com.project.dto.ProjectTasksDTO;
import com.project.dto.ProjectsDTO;
import com.project.dto.Response;
import com.project.enums.AvailableStatus;
import com.project.enums.ProjectTaskStatus;
import com.project.exception.OurException;
import com.project.model.ProjectTasks;
import com.project.model.Projects;
import com.project.repository.ProjectTasksRepository;
import com.project.repository.ProjectsRepository;
import com.project.ultis.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectTasksService {

    @Autowired
    private ProjectTasksRepository projectTasksRepository;
    @Autowired
    private ProjectsRepository projectsRepository;

    public Response createTask(ProjectTasksDTO taskDTO) {
        Response response = new Response();
        try {
            ProjectTasks task = new ProjectTasks();
            task.setTaskName(taskDTO.getTaskName());
            task.setDescription(taskDTO.getDescription());
           // task.setStatus(ProjectTaskStatus.INPROGRESS);
            task.setStatus(taskDTO.getStatus() != null ? taskDTO.getStatus() : ProjectTaskStatus.NOTSTARTED);
            task.setDateCreated(LocalDateTime.now());
            task.setDateUpdated(LocalDateTime.now());
            task.setAvailableStatus(AvailableStatus.ACTIVE);
            Projects project = projectsRepository.findById(taskDTO.getProjects().getId())
                    .orElseThrow(() -> new OurException("Project not found"));

            task.setProjects(project);
            projectTasksRepository.save(task);

            ProjectTasksDTO dto = mapToDTO(task);
            response.setProjectTasksDTOList(Arrays.asList(dto));
            response.setStatusCode(200);
            response.setMessage("Task created successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during task creation: " + e.getMessage());
        }
        return response;
    }

    public Response getAllTasks() {
        Response response = new Response();
        try {
            List<ProjectTasks> tasksList = projectTasksRepository.findByAvailableStatus(AvailableStatus.ACTIVE);
            List<ProjectTasksDTO> tasksDTOList = new ArrayList<>();
            if (!tasksList.isEmpty()) {
                tasksDTOList = tasksList.stream().map(this::mapToDTO).collect(Collectors.toList());
                response.setProjectTasksDTOList(tasksDTOList);
                response.setStatusCode(200);
                response.setMessage("Tasks retrieved successfully");
            } else {
                response.setProjectTasksDTOList(tasksDTOList);
                response.setStatusCode(400);
                response.setMessage("Task not found");
            }

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during retrieving tasks: " + e.getMessage());
        }
        return response;
    }

    public Response getTaskById(Long id) {
        Response response = new Response();
        try {
            ProjectTasks task = projectTasksRepository.findById(id)
                    .orElseThrow(() -> new OurException("Task not found"));
            ProjectTasksDTO dto = mapToDTO(task);
            response.setProjectTasksDTOList(Arrays.asList(dto));
            response.setStatusCode(200);
            response.setMessage("Task retrieved successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during retrieving task: " + e.getMessage());
        }
        return response;
    }

    public Response updateTask(Long id, ProjectTasksDTO taskDTO) {
        Response response = new Response();
        try {
            ProjectTasks task = projectTasksRepository.findById(id)
                    .orElseThrow(() -> new OurException("Task not found"));
            if (taskDTO.getTaskName() != null) {
                task.setTaskName(taskDTO.getTaskName());
            }
            if (taskDTO.getDescription() != null) {
                task.setDescription(taskDTO.getDescription());
            }
            if (taskDTO.getStatus() != null) {
                task.setStatus(taskDTO.getStatus());
            }
            if (taskDTO.getAvailableStatus() != null) {
                task.setAvailableStatus(taskDTO.getAvailableStatus());
            }
            task.setDateUpdated(LocalDateTime.now());
            projectTasksRepository.save(task);

            ProjectTasksDTO dto = mapToDTO(task);
            response.setProjectTasksDTOList(Arrays.asList(dto));
            response.setStatusCode(200);
            response.setMessage("Task updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during task update: " + e.getMessage());
        }
        return response;
    }

    public Response deleteTask(Long id) {
        Response response = new Response();
        try {
            ProjectTasks task = projectTasksRepository.findById(id)
                    .orElseThrow(() -> new OurException("Task not found"));
            task.setAvailableStatus(AvailableStatus.DELETED);
            projectTasksRepository.save(task);
            response.setStatusCode(200);
            response.setMessage("Task deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during task deletion: " + e.getMessage());
        }
        return response;
    }

    private ProjectTasksDTO mapToDTO(ProjectTasks task) {
        ProjectTasksDTO dto = new ProjectTasksDTO();
        dto.setId(task.getId());
        dto.setTaskName(task.getTaskName());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setDateCreated(task.getDateCreated());
        dto.setDateUpdated(task.getDateUpdated());
        dto.setAvailableStatus(task.getAvailableStatus());
        dto.setProjects(mapToProjectsDTO(task.getProjects()));
        return dto;
    }

    private ProjectsDTO mapToProjectsDTO(Projects project) {
        ProjectsDTO dto = new ProjectsDTO();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setDescription(project.getDescription());
        dto.setDateCreated(project.getDateCreated());
        dto.setDateUpdated(project.getDateUpdated());
        dto.setAvailableStatus(project.getAvailableStatus());
        // Exclude projectTasks to avoid infinite loop
        dto.setProjectTasks(null);
        return dto;
    }
}