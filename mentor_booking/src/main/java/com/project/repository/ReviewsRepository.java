
package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long>{
    List<Reviews> findByAvailableStatus(AvailableStatus availableStatus);
    List<Reviews> findByUserId(Long userId);
    List<Reviews> findByUserReceiveId(Long userReceiveId);
}
