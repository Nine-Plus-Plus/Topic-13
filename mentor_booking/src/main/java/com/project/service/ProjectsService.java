package com.project.service;

import com.project.dto.ProjectsDTO;
import com.project.dto.Response;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Projects;
import com.project.model.Topic;
import com.project.model.Group;
import com.project.repository.ProjectsRepository;
import com.project.repository.TopicRepository;
import com.project.repository.GroupRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ProjectsService {

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private GroupRepository groupsRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Response createProject(ProjectsDTO createRequest) {
        Response response = new Response();
        try {
            // Validate createRequest
            if (createRequest == null) {
                throw new OurException("Create request cannot be null");
            }
            if (createRequest.getProjectName() == null || createRequest.getProjectName().isEmpty()) {
                throw new OurException("Project name cannot be null or empty");
            }
            if (createRequest.getTopic() == null || createRequest.getTopic().getId() == null) {
                throw new OurException("Topic ID cannot be null");
            }
            if (createRequest.getGroup() == null || createRequest.getGroup().getId() == null) {
                throw new OurException("Group ID cannot be null");
            }

            // Convert ProjectsDTO Object to Projects Entity Object
            Projects project = modelMapper.map(createRequest, Projects.class);
            project.setPercentage(0); // Set percentage to 0 when creating a new project
            project.setDateCreated(LocalDateTime.now()); // Set dateCreated to current time
            project.setDateUpdated(LocalDateTime.now()); // Set dateUpdated to current time
            project.setStatus(AvailableStatus.ACTIVE); // Set status to ACTIVE when creating a new project

            // Fetch and set the topic
            Topic topic = topicRepository.findById(createRequest.getTopic().getId())
                    .orElseThrow(() -> new OurException("Topic not found"));
            project.setTopic(topic);

            // Fetch and set the group
            Group group = groupsRepository.findById(createRequest.getGroup().getId())
                    .orElseThrow(() -> new OurException("Group not found"));
            project.setGroup(group);

            // Save the project
            projectsRepository.save(project);

            // Convert Projects Entity Object to ProjectsDTO Object
            ProjectsDTO dto = modelMapper.map(project, ProjectsDTO.class);
            response.setProjectsDTO(dto); // Set single ProjectsDTO object
            response.setStatusCode(201);
            response.setMessage("Project created successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during project creation: " + e.getMessage());
        }
        return response;
    }

    public Response getAllProjects() {
        Response response = new Response();
        try {
            List<Projects> projectsList = projectsRepository.findAll();
            List<ProjectsDTO> projectsDTOList = Arrays.asList(modelMapper.map(projectsList, ProjectsDTO[].class));
            response.setProjectsDTOList(projectsDTOList);
            response.setStatusCode(200);
            response.setMessage("Projects retrieved successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during retrieving projects: " + e.getMessage());
        }
        return response;
    }

    public Response getProjectById(Long id) {
        Response response = new Response();
        try {
            Projects project = projectsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found"));
            ProjectsDTO dto = modelMapper.map(project, ProjectsDTO.class);
            response.setProjectsDTO(dto); // Set single ProjectsDTO object
            response.setStatusCode(200);
            response.setMessage("Project retrieved successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during retrieving project: " + e.getMessage());
        }
        return response;
    }

    public Response updateProject(Long id, ProjectsDTO updateRequest) {
        Response response = new Response();
        try {
            Projects project = projectsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found"));
            if (updateRequest.getProjectName() != null) {
                project.setProjectName(updateRequest.getProjectName());
            }
            if (updateRequest.getDescription() != null) {
                project.setDescription(updateRequest.getDescription());
            }
            if (updateRequest.getPercentage() != 0) {
                project.setPercentage(updateRequest.getPercentage());
            }
            project.setDateUpdated(LocalDateTime.now()); // Set dateUpdated to current time
            projectsRepository.save(project);
            ProjectsDTO dto = modelMapper.map(project, ProjectsDTO.class);
            response.setProjectsDTO(dto); // Set single ProjectsDTO object
            response.setStatusCode(200);
            response.setMessage("Project updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during project update: " + e.getMessage());
        }
        return response;
    }

    public Response deleteProject(Long id) {
        Response response = new Response();
        try {
            Projects project = projectsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found"));
            project.setStatus(AvailableStatus.DELETED); // Set status to DELETED
            projectsRepository.save(project);
            response.setStatusCode(200);
            response.setMessage("Project marked as deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during project deletion: " + e.getMessage());
        }
        return response;
    }
}