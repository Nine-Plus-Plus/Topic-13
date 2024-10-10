package com.project.service;

import com.project.dto.ProjectTasksDTO;
import com.project.dto.Response;
import com.project.enums.ProjectTaskStatus;
import com.project.exception.OurException;
import com.project.model.ProjectTasks;
import com.project.model.Projects;
import com.project.repository.ProjectTasksRepository;
import com.project.repository.ProjectsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

//Need Justify
@Service
public class ProjectTasksService {

    @Autowired
    private ProjectTasksRepository projectTasksRepository;
    @Autowired
    private ProjectsRepository projectsRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Response createTask(ProjectTasksDTO taskDTO) {
        Response response = new Response();
        try {
            ProjectTasks task = modelMapper.map(taskDTO, ProjectTasks.class);
            task.setStatus(ProjectTaskStatus.INPROGRESS);
            task.setDateCreated(LocalDateTime.now());
            task.setDateUpdated(LocalDateTime.now());
            Projects project = projectsRepository.findById(taskDTO.getProjects().getId())
                    .orElseThrow(() -> new OurException("Project not found"));
            task.setProjects(project);
            projectTasksRepository.save(task);
            ProjectTasksDTO dto = modelMapper.map(task, ProjectTasksDTO.class);
            response.setProjectTasksDTOList(Arrays.asList(dto));
            response.setStatusCode(201);
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
            List<ProjectTasks> tasksList = projectTasksRepository.findAll();
            List<ProjectTasksDTO> tasksDTOList = Arrays.asList(modelMapper.map(tasksList, ProjectTasksDTO[].class));
            response.setProjectTasksDTOList(tasksDTOList);
            response.setStatusCode(200);
            response.setMessage("Tasks retrieved successfully");
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
            ProjectTasksDTO dto = modelMapper.map(task, ProjectTasksDTO.class);
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
            if (taskDTO.getPercentage() != 0) {
                task.setPercentage(taskDTO.getPercentage());
            }
            if (taskDTO.getStatus() != null) {
                task.setStatus(taskDTO.getStatus());
            }
            task.setDateUpdated(LocalDateTime.now());
            projectTasksRepository.save(task);
            ProjectTasksDTO dto = modelMapper.map(task, ProjectTasksDTO.class);
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

    //Need justify
    public Response deleteTask(Long id) {
        Response response = new Response();
        try {
            ProjectTasks task = projectTasksRepository.findById(id)
                    .orElseThrow(() -> new OurException("Task not found"));
            projectTasksRepository.delete(task);
            response.setStatusCode(204);
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
}
