
package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.model.MentorSchedule;
import com.project.model.Mentors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface MentorScheduleRepository extends JpaRepository<MentorSchedule, Long>{

    List<MentorSchedule> findByAvailableStatus(AvailableStatus availableStatus);

    MentorSchedule findByIdAndAvailableStatus(Long id, AvailableStatus availableStatus);

    boolean existsByMentorAndAvailableFromLessThanEqualAndAvailableToGreaterThanEqual(Mentors mentor, LocalDateTime availableTo, LocalDateTime availableFrom);


}
