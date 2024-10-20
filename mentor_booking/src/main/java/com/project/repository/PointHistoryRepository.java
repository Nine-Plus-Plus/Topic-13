package com.project.repository;

import com.project.model.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByStudentId(Long studentId);

    @Query("SELECT ph FROM PointHistory ph WHERE ph.student.group.id = :groupId")
    List<PointHistory> findByStudentGroupId(@Param("groupId") Long groupId);

    @Query("SELECT SUM(ph.point) FROM PointHistory ph WHERE ph.student.group.id = :groupId")
    Long findTotalPointsByGroupId(@Param("groupId") Long groupId);
}