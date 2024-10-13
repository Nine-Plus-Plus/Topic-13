
package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.enums.MentorScheduleStatus;
import com.project.model.MentorSchedule;
import com.project.model.Mentors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface MentorScheduleRepository extends JpaRepository<MentorSchedule, Long>{

    List<MentorSchedule> findByAvailableStatus(AvailableStatus availableStatus);

    MentorSchedule findByIdAndAvailableStatus(Long id, AvailableStatus availableStatus);

    boolean existsByMentorAndAvailableFromLessThanEqualAndAvailableToGreaterThanEqual(Mentors mentor, LocalDateTime availableTo, LocalDateTime availableFrom);

    @Query("SELECT m FROM MentorSchedule m WHERE m.mentor.id = :mentorId AND m.availableStatus = :availableStatus AND m.status = :status")
    List<MentorSchedule> findByMentorIdAndAvailableStatusAndStatus(@Param("mentorId") Long mentorId, @Param("availableStatus") AvailableStatus availableStatus, @Param("status") MentorScheduleStatus status);
}
