
package com.project.repository;

import com.project.model.Mentors;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorsRepository extends JpaRepository<Mentors, Long>{
    Mentors findByUser_Id(Long userId);

    Optional<Mentors> findByMentorCode(String mentorCode);
}
