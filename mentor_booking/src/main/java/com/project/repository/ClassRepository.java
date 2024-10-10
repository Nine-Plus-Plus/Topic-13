
package com.project.repository;


import com.project.model.Class;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long>{
    Optional<Class> findByMentorId(Long mentorId);
    Optional<Class> findBySemesterId(Long semesterId);
    Optional<Class> findByClassName(String className);
    boolean existsByClassNameAndSemesterId(String className, Long semesterId);

    @Query("SELECT c FROM Class c WHERE c.semester.id = :semesterId")
    List<Class> findClassBySemesterId(@Param("semesterId") Long semesterId);

//    @Query("SELECT c FROM Class c WHERE c.class_name LIKE %:className%")
    List<Class> findByClassNameContainingIgnoreCase(String className);
    
}
