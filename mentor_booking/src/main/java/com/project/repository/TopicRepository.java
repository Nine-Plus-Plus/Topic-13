
package com.project.repository;

import com.project.model.Topic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>{
    Optional<Topic> findByTopicName(String topicName);
    
    @Query("SELECT c FROM Topic c WHERE c.semester.id = :semesterId")
    List<Topic> findTopicsBySemesterId(@Param("semesterId") Long semesterId);
    
//    @Query("SELECT t FROM Topic t WHERE t.topic_name LIKE %:topicName%")
    List<Topic> findByTopicNameContainingIgnoreCase(String topicName);
    
    
}
