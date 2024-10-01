
package com.project.repository;

import com.project.model.Semester;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long>{
    Optional<Semester> findById(Long id);
}
