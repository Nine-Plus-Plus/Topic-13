
package com.project.model;

import com.project.enums.MentorScheduleStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "mentor_schedule")
public class MentorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "available_from")
    private LocalDateTime availableFrom;
    
    @Column(name = "available_to")
    private LocalDateTime availableTo;
    
    @Enumerated(EnumType.STRING)
    private MentorScheduleStatus status;
    
    // Quan hệ ManyToOne với Mentors
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentors mentor;

    // Quan hệ OneToOne với Booking
    @OneToOne(mappedBy = "mentorSchedule", cascade = CascadeType.ALL)
    private Booking booking;

}
