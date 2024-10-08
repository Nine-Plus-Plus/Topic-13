package com.project.model;

import com.project.enums.AvailableStatus;
import com.project.enums.MeetingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "meeting")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private MeetingStatus status;
    
    @Column(name = "link_url")
    private String linkURL;
    
    @Column(name = "date_created")
    private LocalDateTime dateCreated;
    
    @OneToOne
    @JoinColumn(name = "booking_id", unique = true)  // Mỗi meeting liên kết với 1 booking
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(name = "available_status")
    private AvailableStatus availableStatus;
}
