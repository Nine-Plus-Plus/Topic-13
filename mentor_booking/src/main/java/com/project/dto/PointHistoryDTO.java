
package com.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.enums.AvailableStatus;
import com.project.enums.PointHistoryStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PointHistoryDTO {

    private Long id;
    private int point;
    private PointHistoryStatus status;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateCreated;
    private BookingDTO booking;
    private AvailableStatus availableStatus;
}
