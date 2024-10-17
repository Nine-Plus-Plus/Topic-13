package com.project.model;

import com.project.enums.AvailableStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "reviews")
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "comment")
    private String comment;
    
    @Column(name = "rating")
    private int rating;
    
    @Column(name = "date_created")
    private LocalDate dateCreated;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) 
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name = "available_status")
    private AvailableStatus availableStatus;
}
