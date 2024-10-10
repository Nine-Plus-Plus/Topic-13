
package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.model.Semester;

import java.util.List;
import java.util.Optional;

import com.project.model.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long>{
    Optional<Semester> findById(Long id);

    @Query("SELECT s FROM Semester s WHERE s.semesterName = :semesterName AND s.availableStatus = :availableStatus")
    Optional<Semester> findBySemesterName(@Param("semesterName")String semesterName, @Param("availableStatus") AvailableStatus status);

    List<Semester> findByAvailableStatus(AvailableStatus availableStatus);

    @Query("SELECT s FROM Semester s WHERE s.id = :id AND s.availableStatus = :availableStatus")
    Semester findByIdAndAvailableStatus(@Param("id") Long id, @Param("availableStatus") AvailableStatus status);
}
