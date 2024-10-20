
package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.enums.BookingStatus;
import com.project.model.Booking;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{
    Booking findByIdAndAvailableStatusAndStatus(Long id, AvailableStatus availableStatus, BookingStatus status);

    Booking findByIdAndAvailableStatus(Long id, AvailableStatus availableStatus);

    List<Booking> findByAvailableStatusAndStatusAndMentorScheduleId(
            AvailableStatus availableStatus, 
            BookingStatus status,
            Long mentorScheduleId);
    
    List<Booking> findByGroupIdAndAvailableStatusAndStatus(
            Long groupId, 
            AvailableStatus availableStatus, 
            BookingStatus status);
    
    List<Booking> findByAvailableStatusAndStatusAndMentorScheduleIdAndGroupId(
            AvailableStatus availableStatus, 
            BookingStatus status,
            Long mentorScheduleId,
            Long groupId);
    
    List<Booking> findByAvailableStatus(AvailableStatus availableStatus);
    
    @Query("SELECT b FROM Booking b WHERE b.group.aClass.id = :classId")
    List<Booking> findBookingsByClassId(
        @Param("classId") Long classId
    );
    
    List<Booking> findByMentorIdAndStatus(
            Long mentorId,
            BookingStatus status
    );
    
    List<Booking> findByGroupIdAndStatus(
            Long groupId,
            BookingStatus status
    );
    
    List<Booking> findByStatusAndMentorScheduleId(BookingStatus status, Long mentorScheduleId);
    
    @Query("SELECT b FROM Booking b WHERE b.status = :bookingStatus AND b.expiredTime < :currentDateTime")
    List<Booking> findAllByStatusAndExpiredTimeBefore(
        @Param("bookingStatus") BookingStatus bookingStatus, 
        @Param("currentDateTime") LocalDateTime currentDateTime
    );
    
    @Query("SELECT b FROM Booking b WHERE b.status = :bookingStatus AND b.mentorSchedule.availableFrom < :currentDateTime")
    List<Booking> findAllByStatusAndAvailableFromBefore(
        @Param("bookingStatus") BookingStatus bookingStatus, 
        @Param("currentDateTime") LocalDateTime currentDateTime
    );
}
