package com.project.service;

import com.project.dto.ClassDTO;
import com.project.enums.AvailableStatus;
import com.project.model.Class;
import com.project.dto.Response;
import com.project.dto.SemesterDTO;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Semester;
import com.project.model.Students;
import com.project.model.Topic;
import com.project.repository.ClassRepository;
import com.project.repository.SemesterRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.project.repository.StudentsRepository;
import com.project.repository.TopicRepository;
import com.project.ultis.Converter;
import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SemesterService {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MentorsService mentorsService;

    /**
     * Phương thức tạo một học kỳ mới
     */
    public Response createSemester(SemesterDTO createRequest) {
        Response response = new Response();
        try {
            // Kiểm tra xem tên học kỳ đã tồn tại hay chưa
            if (semesterRepository.findBySemesterName(createRequest.getSemesterName(), AvailableStatus.ACTIVE).isPresent()) {
                throw new OurException("Semester has already existed");
            }
            // Kiểm tra xung đột thời gian với các học kỳ đã tồn tại
            List<Semester> overlappingSemesters = semesterRepository.findOverlappingSemesters(
                    createRequest.getDateStart(), createRequest.getDateEnd(), AvailableStatus.ACTIVE);

            if (!overlappingSemesters.isEmpty()) {
                throw new OurException("Semester date range conflicts with an existing semester.");
            }
            // Tạo mới một học kỳ
            Semester semester = new Semester();
            semester.setDateCreated(LocalDateTime.now());
            semester.setSemesterName(createRequest.getSemesterName().trim());
            semester.setDateStart(createRequest.getDateStart());
            semester.setDateEnd(createRequest.getDateEnd());
            semester.setAvailableStatus(AvailableStatus.ACTIVE);
            semesterRepository.save(semester);
            // Xử lý thành công
            if (semester.getId() > 0) {
                SemesterDTO dto = Converter.convertSemesterToSemesterDTO(semester);
                response.setSemesterDTO(dto);
                response.setStatusCode(200);
                response.setMessage("Semester added successfully");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during semester creation: " + e.getMessage());
        }

        return response;
    }

    /**
     * Phương thức lấy danh sách tất cả các học kỳ
     */
    public Response getAllSemesters() {
        Response response = new Response();
        try {
            // Lấy danh sách học kỳ không bị xóa
            List<Semester> semesterList = semesterRepository.findByAvailableStatusNotDeletedOrderByDateStartDesc(AvailableStatus.DELETED);
            List<SemesterDTO> semesterListDTO = new ArrayList<>();
            if (!semesterList.isEmpty()) {
                semesterListDTO = semesterList
                        .stream()
                        .map(Converter::convertSemesterToSemesterDTO)
                        .collect(Collectors.toList());
                response.setSemesterDTOList(semesterListDTO);
                response.setStatusCode(200);
                response.setMessage("Semester fetched successfully");
            } else {
                response.setSemesterDTOList(semesterListDTO);
                response.setStatusCode(400);
                response.setMessage("No data found");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get all semesters " + e.getMessage());
        }
        return response;
    }

    /**
     * Phương thức lấy thông tin học kỳ theo ID
     */
    public Response getSemesterById(Long id) {
        Response response = new Response();
        try {
            Semester findSemester = semesterRepository.findByIdAndAvailableStatus(id, AvailableStatus.ACTIVE);
            SemesterDTO semesterDTO = new SemesterDTO();
            if (findSemester != null) {
                semesterDTO = Converter.convertSemesterToSemesterDTO(findSemester);
                response.setSemesterDTO(semesterDTO);
                response.setStatusCode(200);
                response.setMessage("Successfully");
            } else {
                response.setSemesterDTO(semesterDTO);
                response.setStatusCode(400);
                response.setMessage("No data found");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get semester " + e.getMessage());
        }
        return response;
    }

    /**
     * Phương thức cập nhật thông tin học kỳ
     */
    public Response updateSemester(Long id, SemesterDTO newSemester) {
        Response response = new Response();
        try {
            Semester presentSemester = semesterRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find semester with id: " + id));
            // Kiểm tra xung đột thời gian với các học kỳ đã tồn tại (không bao gồm học kỳ hiện tại)
            List<Semester> overlappingSemesters = semesterRepository.findOverlappingSemestersUpdate(
                    newSemester.getDateStart(), newSemester.getDateEnd(), AvailableStatus.ACTIVE, id);

            if (!overlappingSemesters.isEmpty()) {
                throw new OurException("Semester date range conflicts with an existing semester.");
            }

            // Cập nhật thông tin học kỳ
            if(newSemester.getSemesterName()!=null) presentSemester.setSemesterName(newSemester.getSemesterName().trim());
            if(newSemester.getDateStart()!=null) presentSemester.setDateStart(newSemester.getDateStart());
            if(newSemester.getDateEnd()!=null) presentSemester.setDateEnd(newSemester.getDateEnd());
            semesterRepository.save(presentSemester);

            SemesterDTO dto = Converter.convertSemesterToSemesterDTO(presentSemester);
            response.setSemesterDTO(dto);
            response.setStatusCode(200);
            response.setMessage("Semester updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating semester: " + e.getMessage());
        }
        return response;
    }

    /**
     * Phương thức xóa học kỳ
     */
    public Response deleteSemester(Long id) {
        Response response = new Response();
        try {
            Semester deleteSemester = semesterRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find semester with id: " + id));
            deleteSemester.setAvailableStatus(AvailableStatus.DELETED);
            semesterRepository.save(deleteSemester);

            response.setStatusCode(200);
            response.setMessage("Semester deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting semester: " + id);
        }
        return response;
    }

    /**
     * Phương thức tự động chuyển học kỳ sang trạng thái không hoạt động sau khi hết hạn
     */
    @Scheduled(fixedRate = 60000)  // Chạy mỗi 60 giây (1 phút)
    @Transactional
    public void inactiveSemesterAutomatically() {
        try {
            // Lấy danh sách các học kỳ đã hết hạn và đang ở trạng thái hoạt động
            List<Semester> inactiveSemester = semesterRepository.findExpiredSemester(LocalDate.now(), AvailableStatus.ACTIVE);
            for (Semester s : inactiveSemester) {
                s.setAvailableStatus(AvailableStatus.INACTIVE);

                // Vô hiệu hóa tất cả các lớp và sinh viên trong học kỳ hết hạn
                List<Class> classList = classRepository.findClassBySemesterId(s.getId(), AvailableStatus.ACTIVE);
                for (Class c : classList) {
                    c.setAvailableStatus(AvailableStatus.INACTIVE);
                    c.setMentor(null);
                    List<Students> studentsList = studentsRepository.findStudentByClassId(c.getId(), AvailableStatus.ACTIVE);
                    for (Students std : studentsList) {
                        std.setAvailableStatus(AvailableStatus.INACTIVE);
                        std.getUser().setAvailableStatus(AvailableStatus.INACTIVE);
                    }
                }

                // Vô hiệu hóa tất cả các đề tài liên quan đến học kỳ
                List<Topic> topicList = topicRepository.findTopicsBySemesterIdAndNotDeleted(s.getId(), AvailableStatus.DELETED);
                for (Topic t : topicList) {
                    t.setAvailableStatus(AvailableStatus.INACTIVE);
                }

                // Tạo báo cáo đánh giá mentor
                mentorsService.generateMentorReportRating(s);
                // Lưu thông tin học kỳ đã cập nhật
                semesterRepository.save(s);
            }
            System.out.println("Semester inactive successfully.");
        } catch (Exception e) {
            System.err.println("Error while inactive semester: " + e.getMessage());
        }
    }

}
