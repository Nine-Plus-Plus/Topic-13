package com.project.service;

import com.project.dto.ProjectTasksDTO;
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
import java.util.List;
import java.util.Optional;

@Service
public class ProjectTasksService {

    @Autowired
    private ProjectTasksRepository projectTasksRepository;

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ProjectTasksDTO createTask(ProjectTasksDTO taskDTO) {
        ProjectTasks task = modelMapper.map(taskDTO, ProjectTasks.class);
        task.setStatus(ProjectTaskStatus.NOT_YET);
        task.setDateCreated(LocalDateTime.now());
        task.setDateUpdated(LocalDateTime.now());

        Projects project = projectsRepository.findById(taskDTO.getProjects().getId())
                .orElseThrow(() -> new OurException("Project not found"));
        task.setProjects(project);

        projectTasksRepository.save(task);
        return modelMapper.map(task, ProjectTasksDTO.class);
    }

    public List<ProjectTasksDTO> getAllTasks() {
        List<ProjectTasks> tasks = projectTasksRepository.findAll();
        return Arrays.asList(modelMapper.map(tasks, ProjectTasksDTO[].class));
    }

    public ProjectTasksDTO getTaskById(Long id) {
        ProjectTasks task = projectTasksRepository.findById(id)
                .orElseThrow(() -> new OurException("Task not found"));
        return modelMapper.map(task, ProjectTasksDTO.class);
    }

    public ProjectTasksDTO updateTask(Long id, ProjectTasksDTO taskDTO) {
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
        return modelMapper.map(task, ProjectTasksDTO.class);
    }

    public void deleteTask(Long id) {
        ProjectTasks task = projectTasksRepository.findById(id)
                .orElseThrow(() -> new OurException("Task not found"));
        projectTasksRepository.delete(task);
    }
}