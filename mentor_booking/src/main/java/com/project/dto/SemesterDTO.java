
package com.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

import com.project.enums.AvailableStatus;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SemesterDTO {
    private Long id;
    private String semesterName;
    private LocalDateTime dateCreated;
    private List<ClassDTO> classes;
    private List<TopicDTO> topicDTOS;
    private AvailableStatus availableStatus;
}
