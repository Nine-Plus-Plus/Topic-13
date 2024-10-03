
package com.project.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "topic_name")
    private String topicName;
    
    @Column(name = "context")
    private String context;
    
    @Column(name = "problems")
    private String problems;
    
    @Column(name = "actor")
    private List<String> actor;
    
    @Column(name = "requirement")
    private List<String> requirement;
    
    @Column(name = "non_function_requirement")
    private List<String> nonFunctionRequirement;
    
    @Column(name = "date_created")
    private LocalDateTime dateCreated;
    
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
    
    @OneToOne(mappedBy = "topic", cascade = CascadeType.ALL)
    private Projects project;
    
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Mentors mentor;
    
    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;
}
