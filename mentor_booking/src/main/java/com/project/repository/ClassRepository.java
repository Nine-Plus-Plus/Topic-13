
package com.project.repository;


import com.project.model.Class;
import com.project.model.Mentors;
import com.project.model.Semester;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<com.project.model.Class, Long>{
    Optional<com.project.model.Class> findByMentorId(Long mentorId);
    Optional<com.project.model.Class> findBySemesterId(Long semesterId);
    Optional<com.project.model.Class> findByClassName(String className);
    boolean existsByClassNameAndSemesterId(String className, Long semesterId);

    @Query("SELECT c FROM Class c WHERE c.semester.id = :semesterId")
    List<Class> findClassBySemesterId(@Param("semesterId") Long semesterId);
}
