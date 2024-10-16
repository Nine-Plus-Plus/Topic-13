package com.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.enums.ProjectTaskStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectTasksDTO {
    private Long id;
    private String taskName;
    private String description;
    private float percentage;
    private ProjectTaskStatus status;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    
    @JsonIgnoreProperties({"projectTasks"})
    private ProjectsDTO projects;
}