
package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.model.Mentors;

import java.util.List;
import java.util.Optional;

import com.project.model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorsRepository extends JpaRepository<Mentors, Long>{
    Mentors findByUser_Id(Long userId);

    Optional<Mentors> findByMentorCode(String mentorCode);

    List<Mentors> findByAvailableStatus(AvailableStatus availableStatus);

    Mentors findByIdAndAvailableStatus(Long id, AvailableStatus availableStatus);

    Mentors findByMentorCodeAndAvailableStatus(String mentorCode, AvailableStatus availableStatus);

    @Query("SELECT m FROM Mentors m WHERE m.user.fullName LIKE %:name% AND m.availableStatus = :availableStatus")
    List<Mentors> findByName(
            String name,
            AvailableStatus availableStatus);

    @Query("SELECT DISTINCT m FROM Mentors m JOIN m.skills s WHERE s IN :skills AND m.availableStatus = :availableStatus")
    List<Mentors> findBySkills(
            List<Skills> skills,
            AvailableStatus availableStatus);
}
