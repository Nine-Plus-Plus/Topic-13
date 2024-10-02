package com.project.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "class")
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "class_name")
    private String className;
    
    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;
    
    @Column(name = "date_created")
    private LocalDateTime dateCreated;
    
    @OneToMany(mappedBy = "aClass")
    private List<Students> students; 
    
    @OneToOne
    @JoinColumn(name = "mentor_id", unique = true)  
    private Mentors mentor;

    @OneToMany(mappedBy = "aClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Group> groups;
}
