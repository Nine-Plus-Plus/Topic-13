package com.project.repository;
import com.project.enums.AvailableStatus;
import com.project.model.ProjectTasks;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTasksRepository extends JpaRepository<ProjectTasks, Long> {

    List<ProjectTasks> findByAvailableStatus(AvailableStatus active);
    
}