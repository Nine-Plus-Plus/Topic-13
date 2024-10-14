
package com.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.enums.AvailableStatus;
import com.project.enums.ProjectTaskStatus;
import java.time.LocalDateTime;
import lombok.Data;

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
    private ProjectsDTO projects;
    private AvailableStatus availableStatus;
}
