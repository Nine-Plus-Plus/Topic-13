
package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.model.Students;

import java.util.List;
import java.util.Optional;

import com.project.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentsRepository extends JpaRepository<Students, Long>{
    Optional<Students> findByStudentCode(String studentCode);
    Students findByUser_Id(Long userId);

    @Query("SELECT s FROM Students s WHERE s.user.fullName LIKE %:name% AND s.expertise LIKE :expertise AND s.availableStatus = :availableStatus")
    List<Students> findStudentByUserFullNameAndExpertise(
            @Param("name") String name,
            @Param("expertise") String expertise,
            @Param("availableStatus") AvailableStatus status);

    @Query("SELECT s FROM Students s WHERE s.user.fullName LIKE %:name% AND s.availableStatus = :availableStatus")
    List<Students> findStudentByUserFullName(@Param("name") String name, @Param("availableStatus") AvailableStatus status);

    @Query("SELECT s FROM Students s WHERE s.expertise LIKE :expertise AND s.availableStatus = :availableStatus")
    List<Students> findByExpertise(@Param("expertise") String expertise, @Param("availableStatus") AvailableStatus status);

    List<Students> findByAvailableStatus(AvailableStatus status);

    @Query("SELECT s FROM Students s WHERE s.id = :id AND s.availableStatus = :availableStatus")
    Students findByIdAndAvailableStatus(@Param("id") Long id, @Param("availableStatus") AvailableStatus status);
}
