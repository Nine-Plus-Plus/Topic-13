
package com.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicDTO {
    private Long id;
    private String topicName;
    private String context;
    private String problems;
    private List<String> actor;
    private List<String> requirement;
    private List<String> nonFunctionRequirement;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private ProjectsDTO project;
    private SemesterDTO semesterDTO;
    private MentorsDTO mentorsDTO;
}
