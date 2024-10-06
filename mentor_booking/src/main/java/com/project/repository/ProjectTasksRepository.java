package com.project.repository;

import com.project.model.ProjectTasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTasksRepository extends JpaRepository<ProjectTasks, Long> {
}