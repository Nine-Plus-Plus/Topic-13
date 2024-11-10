package com.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MentorReportDTO {
    private Long id;
    private MentorsDTO mentorsDTO;
    private SemesterDTO semesterDTO;
    private float starRating;
    private LocalDate dateCreated;
}
