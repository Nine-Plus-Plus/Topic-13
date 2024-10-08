
package com.project.repository;


import com.project.model.Mentors;
import com.project.model.Semester;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<com.project.model.Class, Long>{
    Optional<com.project.model.Class> findByMentorId(Long mentorId);
    Optional<com.project.model.Class> findBySemesterId(Long semesterId);
    Optional<com.project.model.Class> findByClassName(String className);
    boolean existsByClassNameAndSemesterId(String className, Long semesterId);
}
