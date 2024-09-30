
package com.project.repository;

import com.project.model.Students;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentsRepository extends JpaRepository<Students, Long>{
    Optional<Students> findByStudentCode(String studentCode);
    Students findByUser_Id(Long userId);
}
