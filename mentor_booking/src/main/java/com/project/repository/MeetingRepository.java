package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.model.Meeting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Meeting findByBookingIdAndAvailableStatus(Long bookingId, AvailableStatus availableStatus);

    @Query("SELECT m FROM Meeting m WHERE m.booking.mentor.user.id = :userId OR EXISTS (SELECT s FROM m.booking.group.students s WHERE s.user.id = :userId)")
    List<Meeting> findMeetingsByUserId(@Param("userId") Long userId);
}
