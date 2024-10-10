package com.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.enums.AvailableStatus;
import com.project.enums.ProjectTaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectTasksDTO {
    private Long id; //primary key
    private String taskName; //User input
    private String description; //User input
    private float percentage; //default value is 0
    private ProjectTaskStatus status; //default value is INPROGRESS
    private LocalDateTime dateCreated; //default value is current time
    private LocalDateTime dateUpdated; //default value is current time
    private ProjectsDTO projects; //foreign key, many to one relationship with projects, user chooses from a list of projects
    private AvailableStatus availableStatus;
}
