package com.project.dto;

import com.project.model.Group;
import com.project.model.Students;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberGroupDTO {
    private Long id;
    private Group group;
    private Students student;
    private String role;
    private String status;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
}