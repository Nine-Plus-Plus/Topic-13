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

    public Response createSemester(SemesterDTO createRequest) {
        Response response = new Response();
        try {
            if (semesterRepository.findBySemesterName(createRequest.getSemesterName(), AvailableStatus.ACTIVE).isPresent()) {
                throw new OurException("Semester has already existed");
            }
            List<Semester> overlappingSemesters = semesterRepository.findOverlappingSemesters(
                    createRequest.getDateStart(), createRequest.getDateEnd(), AvailableStatus.ACTIVE);

            if (!overlappingSemesters.isEmpty()) {
                throw new OurException("Semester date range conflicts with an existing semester.");
            }
            Semester semester = new Semester();
            semester.setDateCreated(LocalDateTime.now());
            semester.setSemesterName(createRequest.getSemesterName().trim());
            semester.setDateStart(createRequest.getDateStart());
            semester.setDateEnd(createRequest.getDateEnd());
            semester.setAvailableStatus(AvailableStatus.ACTIVE);
            semesterRepository.save(semester);
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

    // phương thức tìm tất cả Semester
    public Response getAllSemesters(){
        Response response = new Response();
        try {
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
            }else{
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

    // phương thức tìm Semester theo Id
    public Response getSemesterById(Long id){
        Response response = new Response();
        try {
            Semester findSemester = semesterRepository.findByIdAndAvailableStatus(id,AvailableStatus.ACTIVE);
            SemesterDTO semesterDTO = new SemesterDTO();
            if (findSemester != null) {
                semesterDTO = Converter.convertSemesterToSemesterDTO(findSemester);
                response.setSemesterDTO(semesterDTO);
                response.setStatusCode(200);
                response.setMessage("Successfully");
            }else{
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

    // phương thức cập nhập mới Semester
    public Response updateSemester(Long id, SemesterDTO newSemester){
        Response response = new Response();
        try {
            Semester presentSemester = semesterRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find semester with id: "+id));
            List<Semester> overlappingSemesters = semesterRepository.findOverlappingSemestersUpdate(
                    newSemester.getDateStart(), newSemester.getDateEnd(), AvailableStatus.ACTIVE, id);

            if (!overlappingSemesters.isEmpty()) {
                throw new OurException("Semester date range conflicts with an existing semester.");
            }
            presentSemester.setSemesterName(newSemester.getSemesterName().trim());
            presentSemester.setDateStart(newSemester.getDateStart());
            presentSemester.setDateEnd(newSemester.getDateEnd());
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

    // phương thức xóa Semester
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

    @Scheduled(fixedRate = 60000)  // Chạy mỗi 60 giây (1 phút)
    @Transactional
    public void inactiveSemesterAutomatically(){
        try {
            List<Semester> inactiveSemester = semesterRepository.findExpiredSemester(LocalDate.now(), AvailableStatus.ACTIVE);
            for(Semester s : inactiveSemester){
                s.setAvailableStatus(AvailableStatus.INACTIVE);

                List<Class> classList = classRepository.findClassBySemesterId(s.getId(), AvailableStatus.ACTIVE);
                for(Class c : classList){
                    c.setAvailableStatus(AvailableStatus.INACTIVE);
                    c.setMentor(null);
                    List<Students> studentsList = studentsRepository.findStudentByClassId(c.getId(), AvailableStatus.ACTIVE);
                    for(Students std : studentsList){
                        std.setAvailableStatus(AvailableStatus.INACTIVE);
                        std.getUser().setAvailableStatus(AvailableStatus.INACTIVE);
                    }
                }

                List<Topic> topicList = topicRepository.findTopicsBySemesterIdAndNotDeleted(s.getId(), AvailableStatus.DELETED);
                for(Topic t : topicList){
                    t.setAvailableStatus(AvailableStatus.INACTIVE);
                }

                mentorsService.generateMentorReportRating(s);

                semesterRepository.save(s);
            }
            System.out.println("Semester inactive successfully.");
        } catch (Exception e) {
            System.err.println("Error while inactive semester: " + e.getMessage());
        }
    }

}
