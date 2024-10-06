package com.project.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "projects")
public class Projects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_name")
    private String projectName;
    
    @Column(name = "percentage", nullable = false)
    private float percentage = 0; // default value set = 0
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;
    
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
        dateUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateUpdated = LocalDateTime.now();
    }
    
    @OneToMany(mappedBy = "projects", cascade = CascadeType.ALL, fetch = FetchType.LAZY)  // Thiết lập mối quan hệ OneToMany
    private List<ProjectTasks> projectTasks;
    
    @OneToOne
    @JoinColumn(name = "topic_id", unique = true) // Khóa ngoại trỏ tới bảng Topic
    private Topic topic;
    
    @OneToOne
    @JoinColumn(name = "group_id", unique = true)
    private Group group;
}
