
package com.project.repository;

import com.project.model.Students;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentsRepository extends JpaRepository<Students, Long>{
    Optional<Students> findByStudentCode(String studentCode);
    Students findByUser_Id(Long userId);

    @Query("SELECT s FROM Students s WHERE s.user.fullName LIKE %:name% AND s.expertise LIKE :expertise")
    List<Students> findStudentByUserFullNameAndExpertise(@Param("name") String name, @Param("expertise") String expertise);

    @Query("SELECT s FROM Students s WHERE s.user.fullName LIKE %:name%")
    List<Students> findStudentByUserFullName(@Param("name") String name);

    @Query("SELECT s FROM Students s WHERE s.expertise LIKE :expertise")
    List<Students> findByExpertise(@Param("expertise") String expertise);
}
