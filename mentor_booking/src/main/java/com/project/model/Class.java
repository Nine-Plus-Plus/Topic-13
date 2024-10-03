package com.project.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    
    @OneToMany(mappedBy = "aClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Group> groups;
    
    @OneToOne
    @JoinColumn(name = "mentor_id", unique = true)  
    private Mentors mentor; 
}
