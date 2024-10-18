package com.project.service;

import com.project.dto.BookingDTO;
import com.project.dto.Response;
import com.project.enums.AvailableStatus;
import com.project.enums.BookingStatus;
import com.project.enums.MentorScheduleStatus;
import com.project.enums.PointHistoryStatus;
import com.project.exception.OurException;
import com.project.model.Booking;
import com.project.model.Group;
import com.project.model.MentorSchedule;
import com.project.model.Mentors;
import com.project.model.PointHistory;
import com.project.model.Students;
import com.project.repository.BookingRepository;
import com.project.repository.GroupRepository;
import com.project.repository.MentorScheduleRepository;
import com.project.repository.MentorsRepository;
import com.project.repository.PointHistoryRepository;
import com.project.ultis.Converter;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thịnh Đạt
 */
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MentorScheduleRepository mentorScheduleRepository;

    @Autowired
    private MentorsRepository mentorsRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    public Response createBooking(BookingDTO createRequest) {
        Response response = new Response();
        try {
            if (createRequest.getMentorSchedule() == null) {
                throw new OurException("Cannot find schedule");
            }
            //The group has already have a confirmed booking
            if (!bookingRepository.findByGroupIdAndAvailableStatusAndStatus(createRequest.getGroup().getId(), AvailableStatus.ACTIVE, BookingStatus.CONFIRMED).isEmpty()) {
                throw new OurException("You already have a booking that is confirmed");
            }

            //This mentor has accepted another group's booking
            if (!bookingRepository.findByAvailableStatusAndStatusAndMentorScheduleId(AvailableStatus.ACTIVE, BookingStatus.CONFIRMED, createRequest.getMentorSchedule().getId()).isEmpty()) {
                throw new OurException("The mentor has already have a meeting with this schedule");
            }

            //This group has already booked this mentor with the same schedule, prevent spamming
            if (!bookingRepository.findByAvailableStatusAndStatusAndMentorScheduleIdAndGroupId(AvailableStatus.ACTIVE, BookingStatus.PENDING,
                    createRequest.getMentorSchedule().getId(), createRequest.getGroup().getId()).isEmpty()) {
                throw new OurException("You have booked this mentor with this schedule");
            }

            MentorSchedule mentorSchedule = mentorScheduleRepository.findByIdAndAvailableStatus(createRequest.getMentorSchedule().getId(), AvailableStatus.ACTIVE);
            Mentors mentor = mentorsRepository.findByIdAndAvailableStatus(mentorSchedule.getMentor().getId(), AvailableStatus.ACTIVE);
            Group group = groupRepository.findByIdAndAvailableStatus(createRequest.getGroup().getId(), AvailableStatus.ACTIVE);

            LocalDateTime timeStart = mentorSchedule.getAvailableFrom();
            LocalDateTime timeEnd = mentorSchedule.getAvailableTo();
            int time = (int) timeStart.until(timeEnd, ChronoUnit.MINUTES);

            if (mentor.getTotalTimeRemain() < time) {
                throw new OurException("This mentor has reached their support time this semester");
            }

            time /= 30;

            int pointPay = group.getStudents().size() * 10 * (int) time;

            if (group.getTotalPoint() - pointPay < 0) {
                throw new OurException("Your group doesn't have enough points to make a booking");
            }

            Booking booking = new Booking();
            booking.setDateCreated(LocalDateTime.now());
            booking.setDateUpdated(LocalDateTime.now());
            booking.setStatus(BookingStatus.PENDING);
            booking.setMentorSchedule(mentorSchedule);
            booking.setMentor(mentor);
            booking.setGroup(group);
            booking.setPointPay(pointPay);
            booking.setAvailableStatus(AvailableStatus.ACTIVE);

            booking.setExpiredTime(LocalDateTime.now().plusHours(time));

            bookingRepository.save(booking);

            if (booking.getId() > 0) {
                BookingDTO dto = Converter.convertBookingToBookingDTO(booking);
                response.setBookingDTO(dto);
                response.setStatusCode(200);
                response.setMessage("Create booking successfully");
            }

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during booking creation: " + e.getMessage());
        }
        return response;
    }

    /**
     * Get all ACTIVE bookings (usually PENDING and not started CONFIRMED
     * bookings)
     *
     * @return all bookings have ACTIVE available status
     */
    public Response getAllActiveBookings() {
        Response response = new Response();
        try {
            List<Booking> bookingList = bookingRepository.findByAvailableStatus(AvailableStatus.ACTIVE);
            List<BookingDTO> bookingListDTO = new ArrayList<>();
            if (!bookingList.isEmpty()) {
                bookingListDTO = bookingList.stream()
                        .map(Converter::convertBookingToBookingDTO)
                        .collect(Collectors.toList());

                response.setBookingDTOList(bookingListDTO);
                response.setStatusCode(200);
                response.setMessage("Bookings fetched successfully");
            } else {
                response.setBookingDTOList(bookingListDTO);
                response.setStatusCode(400);
                response.setMessage("Cannot find any booking");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get all bookings: " + e.getMessage());
        }
        return response;
    }

    /**
     * Get all INACTIVE bookings (Usually done CONFIRMED, REJECTED or CANCELED
     * bookings)
     *
     * @return all INACTIVE bookings (or in another way, all CONFIRMED bookings
     * that have been started before and REJECTED, CANCELED bookings)
     */
    public Response getAllOldBookings() {
        Response response = new Response();
        try {
            List<Booking> bookingList = bookingRepository.findByAvailableStatus(AvailableStatus.INACTIVE);
            List<BookingDTO> bookingListDTO = new ArrayList<>();
            if (!bookingList.isEmpty()) {
                bookingListDTO = bookingList.stream()
                        .map(Converter::convertBookingToBookingDTO)
                        .collect(Collectors.toList());

                response.setBookingDTOList(bookingListDTO);
                response.setStatusCode(200);
                response.setMessage("Bookings fetched successfully");
            } else {
                response.setBookingDTOList(bookingListDTO);
                response.setStatusCode(400);
                response.setMessage("Cannot find any booking");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get all bookings: " + e.getMessage());
        }
        return response;
    }

    public Response getBookingsInClass(Long classId) {
        Response response = new Response();
        try {
            List<Booking> bookingList = bookingRepository.findBookingsByClassId(classId);
            List<BookingDTO> bookingListDTO = new ArrayList<>();
            if (!bookingList.isEmpty()) {
                bookingListDTO = bookingList.stream()
                        .map(Converter::convertBookingToBookingDTO)
                        .collect(Collectors.toList());

                response.setBookingDTOList(bookingListDTO);
                response.setStatusCode(200);
                response.setMessage("Bookings fetched successfully");
            } else {
                response.setBookingDTOList(bookingListDTO);
                response.setStatusCode(400);
                response.setMessage("Cannot find any booking");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get all bookings: " + e.getMessage());
        }
        return response;
    }

    public Response acceptBooking(Long bookingId) {
        Response response = new Response();
        try {
            Booking booking = bookingRepository.findByIdAndAvailableStatusAndStatus(bookingId, AvailableStatus.ACTIVE, BookingStatus.PENDING);
            if (booking != null) {
                booking.setStatus(BookingStatus.CONFIRMED);
                booking.setDateUpdated(LocalDateTime.now());
                booking.setExpiredTime(null);
                bookingRepository.save(booking);

                List<Booking> pendingBookingList = bookingRepository.findByStatusAndMentorScheduleId(BookingStatus.PENDING, booking.getMentorSchedule().getId());
                if (!pendingBookingList.isEmpty()) {
                    for (Booking pendingBookings : pendingBookingList) {
                        pendingBookings.setStatus(BookingStatus.REJECTED);
                        pendingBookings.setAvailableStatus(AvailableStatus.INACTIVE);
                        pendingBookings.setDateUpdated(LocalDateTime.now());
                        booking.setExpiredTime(null);
                        bookingRepository.save(pendingBookings);
                    }
                }

                PointHistory pointHistory = new PointHistory();
                pointHistory.setStatus(PointHistoryStatus.REDEEMED);
                pointHistory.setAvailableStatus(AvailableStatus.ACTIVE);
                pointHistory.setBooking(booking);
                pointHistory.setDateCreated(LocalDateTime.now());
                pointHistory.setDateUpdated(LocalDateTime.now());
                pointHistory.setPoint(booking.getPointPay());
                pointHistoryRepository.save(pointHistory);

                List<PointHistory> pointHistoryList;
                if (booking.getPointHistories() == null) {
                    pointHistoryList = new ArrayList<>();
                } else {
                    pointHistoryList = booking.getPointHistories();
                }
                pointHistoryList.add(pointHistory);
                booking.setPointHistories(pointHistoryList);

                List<Students> groupMembers = booking.getGroup().getStudents();

                Group group = groupRepository.findByIdAndAvailableStatus(booking.getGroup().getId(), AvailableStatus.ACTIVE);
                group.setStudents(groupMembers);
                group.setTotalPoint(group.getTotalPoint() - booking.getPointPay());

                int newPoint = group.getTotalPoint() / groupMembers.size();

                for (Students member : groupMembers) {
                    member.setPoint(newPoint);
                }

                MentorSchedule schedule = mentorScheduleRepository.findByIdAndAvailableStatus(booking.getMentorSchedule().getId(), AvailableStatus.ACTIVE);
                schedule.setStatus(MentorScheduleStatus.BOOKED);

                Mentors mentor = mentorsRepository.findByIdAndAvailableStatus(booking.getMentorSchedule().getMentor().getId(), AvailableStatus.ACTIVE);
                LocalDateTime timeStart = schedule.getAvailableFrom();
                LocalDateTime timeEnd = schedule.getAvailableTo();
                int time = (int) timeStart.until(timeEnd, ChronoUnit.MINUTES);
                mentor.setTotalTimeRemain(mentor.getTotalTimeRemain() - time);
                mentorsRepository.save(mentor);

                mentorScheduleRepository.save(schedule);

                groupRepository.save(group);
                bookingRepository.save(booking);

                BookingDTO dto = Converter.convertBookingToBookingDTO(booking);
                response.setBookingDTO(dto);
                response.setStatusCode(200);
                response.setMessage("Booking accepted");
            } else {
                throw new OurException("Cannot find booking");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during accept booking: " + e.getMessage());
        }
        return response;
    }

    public Response rejectBooking(Long bookingId) {
        Response response = new Response();
        try {
            Booking booking = bookingRepository.findByIdAndAvailableStatusAndStatus(bookingId, AvailableStatus.ACTIVE, BookingStatus.PENDING);
            if (booking != null) {
                booking.setStatus(BookingStatus.REJECTED);
                booking.setAvailableStatus(AvailableStatus.INACTIVE);
                booking.setDateUpdated(LocalDateTime.now());
                booking.setExpiredTime(null);

                bookingRepository.save(booking);

                BookingDTO dto = Converter.convertBookingToBookingDTO(booking);
                response.setBookingDTO(dto);
                response.setStatusCode(200);
                response.setMessage("Booking rejected");
            } else {
                throw new OurException("Cannot find booking");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during reject booking: " + e.getMessage());
        }
        return response;
    }

    public Response cancelBooking(Long bookingId, String type) {
        Response response = new Response();
        try {
            Booking booking = bookingRepository.findByIdAndAvailableStatusAndStatus(bookingId, AvailableStatus.ACTIVE, BookingStatus.CONFIRMED);
            if (booking != null) {
                if (type.equalsIgnoreCase("MENTOR")) {
                    booking.setStatus(BookingStatus.CANCELLED);
                    booking.setAvailableStatus(AvailableStatus.INACTIVE);
                    booking.setDateUpdated(LocalDateTime.now());
                    booking.setExpiredTime(null);

                    PointHistory pointHistory = new PointHistory();
                    pointHistory.setStatus(PointHistoryStatus.ADJUSTED);
                    pointHistory.setAvailableStatus(AvailableStatus.ACTIVE);
                    pointHistory.setBooking(booking);
                    pointHistory.setDateCreated(LocalDateTime.now());
                    pointHistory.setDateUpdated(LocalDateTime.now());
                    pointHistory.setPoint(booking.getPointPay());
                    pointHistoryRepository.save(pointHistory);

                    List<PointHistory> pointHistoryList;
                    if (booking.getPointHistories() == null) {
                        pointHistoryList = new ArrayList<>();
                    } else {
                        pointHistoryList = booking.getPointHistories();
                    }
                    pointHistoryList.add(pointHistory);
                    booking.setPointHistories(pointHistoryList);

                    List<Students> groupMembers = booking.getGroup().getStudents();

                    Group group = groupRepository.findByIdAndAvailableStatus(booking.getGroup().getId(), AvailableStatus.ACTIVE);
                    group.setStudents(groupMembers);
                    group.setTotalPoint(group.getTotalPoint() + booking.getPointPay());

                    int newPoint = group.getTotalPoint() / groupMembers.size();

                    for (Students member : groupMembers) {
                        member.setPoint(newPoint);
                    }

                    MentorSchedule schedule = mentorScheduleRepository.findByIdAndAvailableStatus(booking.getMentorSchedule().getId(), AvailableStatus.ACTIVE);
                    schedule.setStatus(MentorScheduleStatus.AVAILABLE);
                    Mentors mentor = mentorsRepository.findByIdAndAvailableStatus(booking.getMentorSchedule().getMentor().getId(), AvailableStatus.ACTIVE);
                    LocalDateTime timeStart = schedule.getAvailableFrom();
                    LocalDateTime timeEnd = schedule.getAvailableTo();
                    int time = (int) timeStart.until(timeEnd, ChronoUnit.MINUTES);
                    mentor.setTotalTimeRemain(mentor.getTotalTimeRemain() + time);
                    mentorsRepository.save(mentor);
                    mentorScheduleRepository.save(schedule);

                    groupRepository.save(group);
                    bookingRepository.save(booking);

                    BookingDTO dto = Converter.convertBookingToBookingDTO(booking);
                    response.setBookingDTO(dto);
                    response.setStatusCode(200);
                    response.setMessage("Booking canceled by mentor");
                }
                if (type.equalsIgnoreCase("STUDENTS")) {
                    booking.setStatus(BookingStatus.CANCELLED);
                    booking.setAvailableStatus(AvailableStatus.INACTIVE);
                    booking.setDateUpdated(LocalDateTime.now());
                    booking.setExpiredTime(null);

                    PointHistory pointHistory = new PointHistory();
                    pointHistory.setStatus(PointHistoryStatus.EXPIRED);
                    pointHistory.setAvailableStatus(AvailableStatus.ACTIVE);
                    pointHistory.setBooking(booking);
                    pointHistory.setDateCreated(LocalDateTime.now());
                    pointHistory.setDateUpdated(LocalDateTime.now());
                    pointHistory.setPoint(booking.getPointPay());
                    pointHistoryRepository.save(pointHistory);

                    List<PointHistory> pointHistoryList;
                    if (booking.getPointHistories() == null) {
                        pointHistoryList = new ArrayList<>();
                    } else {
                        pointHistoryList = booking.getPointHistories();
                    }
                    pointHistoryList.add(pointHistory);
                    booking.setPointHistories(pointHistoryList);

                    MentorSchedule schedule = mentorScheduleRepository.findByIdAndAvailableStatus(booking.getMentorSchedule().getId(), AvailableStatus.ACTIVE);
                    schedule.setStatus(MentorScheduleStatus.AVAILABLE);
                    Mentors mentor = mentorsRepository.findByIdAndAvailableStatus(booking.getMentorSchedule().getMentor().getId(), AvailableStatus.ACTIVE);
                    LocalDateTime timeStart = schedule.getAvailableFrom();
                    LocalDateTime timeEnd = schedule.getAvailableTo();
                    int time = (int) timeStart.until(timeEnd, ChronoUnit.MINUTES);
                    mentor.setTotalTimeRemain(mentor.getTotalTimeRemain() + time);
                    mentorsRepository.save(mentor);
                    mentorScheduleRepository.save(schedule);

                    bookingRepository.save(booking);

                    BookingDTO dto = Converter.convertBookingToBookingDTO(booking);
                    response.setBookingDTO(dto);
                    response.setStatusCode(200);
                    response.setMessage("Booking canceled by students");
                }
            } else {
                throw new OurException("Cannot find booking");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during cancel booking: " + e.getMessage());
        }
        return response;
    }

    public Response getBookingsByMentorId(Long mentorId, BookingStatus status) {
        Response response = new Response();
        try {
            List<Booking> bookingList = bookingRepository.findByMentorIdAndStatus(mentorId, status);
            List<BookingDTO> bookingListDTO = new ArrayList<>();
            if (!bookingList.isEmpty()) {
                bookingListDTO = bookingList.stream()
                        .map(Converter::convertBookingToBookingDTO)
                        .collect(Collectors.toList());

                response.setBookingDTOList(bookingListDTO);
                response.setStatusCode(200);
                response.setMessage("Bookings fetched successfully");
            } else {
                response.setBookingDTOList(bookingListDTO);
                response.setStatusCode(400);
                response.setMessage("Cannot find any booking");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get all bookings: " + e.getMessage());
        }
        return response;
    }

    public Response getBookingsByGroupId(Long groupId, BookingStatus status) {
        Response response = new Response();
        try {
            List<Booking> bookingList = bookingRepository.findByGroupIdAndStatus(groupId, status);
            List<BookingDTO> bookingListDTO = new ArrayList<>();
            if (!bookingList.isEmpty()) {
                bookingListDTO = bookingList.stream()
                        .map(Converter::convertBookingToBookingDTO)
                        .collect(Collectors.toList());

                response.setBookingDTOList(bookingListDTO);
                response.setStatusCode(200);
                response.setMessage("Bookings fetched successfully");
            } else {
                response.setBookingDTOList(bookingListDTO);
                response.setStatusCode(400);
                response.setMessage("Cannot find any booking");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get all bookings: " + e.getMessage());
        }
        return response;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void setBookingAvailableStatusAndStatusAutomatically() {
        try {
            List<Booking> bookingList;

            //Check if the pending bookings are at the expired time
            bookingList = bookingRepository.findAllByStatusAndExpiredTimeBefore(BookingStatus.PENDING, LocalDateTime.now());
            if (!bookingList.isEmpty()) {
                for (Booking booking : bookingList) {
                    booking.setStatus(BookingStatus.REJECTED);
                    booking.setAvailableStatus(AvailableStatus.INACTIVE);
                    booking.setDateUpdated(LocalDateTime.now());
                    booking.setExpiredTime(null);
                    bookingRepository.save(booking);
                }
            }

            //Check if the booking is not accepted by mentor before the meeting start time
            bookingList = bookingRepository.findAllByStatusAndAvailableFromBefore(BookingStatus.PENDING, LocalDateTime.now().minusMinutes(30));
            if (!bookingList.isEmpty()) {
                for (Booking booking : bookingList) {
                    booking.setStatus(BookingStatus.REJECTED);
                    booking.setAvailableStatus(AvailableStatus.INACTIVE);
                    booking.setDateUpdated(LocalDateTime.now());
                    booking.setExpiredTime(null);
                    bookingRepository.save(booking);
                }
            }

            //Set confirmed active bookings to inactive (The meeting has been started)
            bookingList = bookingRepository.findAllByStatusAndAvailableFromBefore(BookingStatus.CONFIRMED, LocalDateTime.now());
            if (!bookingList.isEmpty()) {
                for (Booking booking : bookingList) {
                    booking.setAvailableStatus(AvailableStatus.INACTIVE);
                    booking.setDateUpdated(LocalDateTime.now());
                    booking.setExpiredTime(null);
                    bookingRepository.save(booking);
                }
            }

        } catch (Exception e) {
            System.out.println("Error while setting booking status automatically: " + e.getMessage());
        }
    }
}
