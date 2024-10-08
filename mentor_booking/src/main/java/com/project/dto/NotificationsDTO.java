package com.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.enums.AvailableStatus;
import com.project.enums.NoficationStatus;
import com.project.enums.NoficationType;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationsDTO {
    private Long id;
    private NoficationType type;
    private NoficationStatus status;
    private String message;
    private LocalDateTime dateTimeCreated;
    private LocalDateTime dateTimeSent;
    private UsersDTO user;
    private AvailableStatus availableStatus;
}
