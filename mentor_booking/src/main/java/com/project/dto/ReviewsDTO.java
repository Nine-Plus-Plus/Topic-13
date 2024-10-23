
package com.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

import com.project.enums.AvailableStatus;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewsDTO {
    private Long id;
    private String comment;
    private int rating;
    private LocalDateTime dateCreated;
    private UsersDTO user;
    private UsersDTO userReceive;
    private AvailableStatus availableStatus;
}
