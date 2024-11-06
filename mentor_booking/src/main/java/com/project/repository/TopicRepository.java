package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.model.Topic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByTopicNameAndAvailableStatus(String topicName, AvailableStatus availableStatus);

    //ADMIN
    @Query("SELECT t FROM Topic t WHERE t.semester.id = :semesterId " +
            "AND t.availableStatus <> :deletedStatus")
    List<Topic> findTopicsBySemesterIdAndNotDeleted(@Param("semesterId") Long semesterId,
                                                    @Param("deletedStatus") AvailableStatus deletedStatus);

    @Query("SELECT t FROM Topic t " +
            "WHERE t.semester.id = :semesterId " +
            "AND t.topicName LIKE %:topicName% " +
            "AND t.availableStatus <> :deletedStatus")
    List<Topic> findTopicsBySemesterIdAndTopicNameNotDeleted(
            @Param("semesterId") Long semesterId,
            @Param("topicName") String name,
            @Param("deletedStatus") AvailableStatus deletedStatus);

//    @Query("SELECT t FROM Topic t WHERE t.topic_name LIKE %:topicName%")
    List<Topic> findByTopicNameContainingIgnoreCaseAndAvailableStatus(String topicName, AvailableStatus availableStatus);

    List<Topic> findByAvailableStatus(AvailableStatus availableStatus);

    Topic findByIdAndAvailableStatus(Long id, AvailableStatus availableStatus);

    @Query("SELECT t FROM Topic t WHERE t.id NOT IN (SELECT g.project.topic.id FROM Group g WHERE g.aClass.id = :classId) AND t.semester.id = :semesterId")
    List<Topic> findUnchosenTopicsInClass(@Param("classId") Long classId, @Param("semesterId") Long semesterId);
}

