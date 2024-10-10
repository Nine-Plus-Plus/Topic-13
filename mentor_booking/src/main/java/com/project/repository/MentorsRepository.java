
package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.model.Mentors;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorsRepository extends JpaRepository<Mentors, Long>{
    Mentors findByUser_Id(Long userId);

    Optional<Mentors> findByMentorCode(String mentorCode);

    List<Mentors> findByAvailableStatus(AvailableStatus availableStatus);

    Mentors findByIdAndAvailableStatus(Long id, AvailableStatus availableStatus);
}
