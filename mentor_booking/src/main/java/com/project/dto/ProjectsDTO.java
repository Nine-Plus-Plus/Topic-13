
package com.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectsDTO {
  
    private Long id; //primary key
    private String projectName; //User input
    private float percentage;   //default value is 0
    private String description; //User input
    private LocalDateTime dateCreated; //default value is current time
    private LocalDateTime dateUpdated; //default value is current time
    private List<ProjectTasksDTO> projectTasks; //one to many relationship with project tasks, foreign key
    private TopicDTO topic; //foreign key, many to one relationship with topic, user chooses from a list of topics
    private GroupDTO group; //foreign key, 
    private String status; //soft delete purpose, default value is ACTIVE
}
