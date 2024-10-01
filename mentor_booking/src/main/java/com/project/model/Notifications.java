package com.project.model;

import com.project.enums.NoficationStatus;
import com.project.enums.NoficationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "notifications")
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NoficationType type;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NoficationStatus status; 
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "date_Time_created")
    private LocalDateTime dateTimeCreated;
    
    @Column(name = "date_Time_sent")
    private LocalDateTime dateTimeSent;
    
    @ManyToOne
    @JoinColumn(name = "user_id") // Thiết lập cột khóa ngoại
    private Users user;

    // Thêm thông tin người nhận
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Users receiver;
}
