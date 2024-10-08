
package com.project.repository;

import com.project.model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillsRepository extends JpaRepository<Skills, Long>{

    Optional<Skills> findBySkillName(String skillName);
}
