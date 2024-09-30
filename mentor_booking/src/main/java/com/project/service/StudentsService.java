package com.project.service;

import com.project.dto.Response;
import com.project.dto.StudentsDTO;
import com.project.exception.OurException;
import com.project.model.Students;
import com.project.repository.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentsService {

    @Autowired
    private StudentsRepository studentsRepository;

    // Phương thức tạo mới sinh viên
    public Response createStudent(StudentsDTO studentsDTO) {
        Response response = new Response();
        try {
            Students student = new Students();
            student.setStudentCode(studentsDTO.getStudentCode());
            student.setExpertise(studentsDTO.getExpertise());
            student.setDateCreated(LocalDate.now());
            student.setGroup(null); // Để group_id null
            student.setAClass(null); // Để class_id null

            studentsRepository.save(student);

            StudentsDTO dto = studentToStudentDTO(student);
            response.setStudentsDTO(dto);
            response.setStatusCode(200);
            response.setMessage("Student created successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during student creation: " + e.getMessage());
        }
        return response;
    }

    // Phương thức lấy tất cả sinh viên
    public Response getAllStudents() {
        Response response = new Response();
        try {
            List<Students> list = studentsRepository.findAll();
            if (!list.isEmpty()) {
                List<StudentsDTO> listDTO = list.stream()
                        .map(this::studentToStudentDTO)
                        .collect(Collectors.toList());

                response.setStudentsDTOList(listDTO);
                response.setStatusCode(200);
                response.setMessage("Students fetched successfully");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching students: " + e.getMessage());
        }
        return response;
    }

    // Phương thức lấy sinh viên theo ID
    public Response getStudentById(Long id) {
        Response response = new Response();
        try {
            Students student = studentsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Student not found with id: " + id));

            StudentsDTO dto = studentToStudentDTO(student);

            response.setStudentsDTO(dto);
            response.setStatusCode(200);
            response.setMessage("Student fetched successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching student: " + e.getMessage());
        }
        return response;
    }

    // Phương thức cập nhật sinh viên
    public Response updateStudent(Long id, StudentsDTO studentsDTO) {
        Response response = new Response();
        try {
            Students student = studentsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Student not found with id: " + id));

            student.setExpertise(studentsDTO.getExpertise());
            student.setStudentCode(studentsDTO.getStudentCode());
            student.setDateUpdated(LocalDate.now());

            studentsRepository.save(student);

            StudentsDTO dto = studentToStudentDTO(student);
            response.setStudentsDTO(dto);
            response.setStatusCode(200);
            response.setMessage("Student updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during student update: " + e.getMessage());
        }
        return response;
    }

    // Phương thức xóa sinh viên
    public Response deleteStudent(Long id) {
        Response response = new Response();
        try {
            Students student = studentsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Student not found with id: " + id));

            studentsRepository.delete(student);
            response.setStatusCode(200);
            response.setMessage("Student deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting student: " + e.getMessage());
        }
        return response;
    }

    // Phương thức chuyển đổi từ Students sang StudentsDTO
    private StudentsDTO studentToStudentDTO(Students student) {
        StudentsDTO dto = new StudentsDTO();
        dto.setId(student.getId());
        dto.setStudentCode(student.getStudentCode());
        dto.setExpertise(student.getExpertise());
        dto.setDateCreated(student.getDateCreated());
        dto.setDateUpdated(student.getDateUpdated());
        return dto;
    }

}
