
package com.project.repository;


import com.project.model.Mentors;
import com.project.model.Semester;
import com.project.model.Class;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long>{
    Optional<Mentors> findByMentorId(Long mentorId);
    Optional<Semester> findBySemesterId(Long semesterId);
    Optional<Class> findByClassName(String className);
}
