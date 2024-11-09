package com.project.repository;

import com.project.model.MentorReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorReportRepository extends JpaRepository<MentorReport, Long> {
    
    List<MentorReport> findBySemesterId(Long semesterId);
}
