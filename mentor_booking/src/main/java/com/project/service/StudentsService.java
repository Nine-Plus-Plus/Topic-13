package com.project.service;

import com.project.dto.Response;
import com.project.dto.StudentsDTO;
import com.project.dto.UsersDTO;
import com.project.exception.OurException;
import com.project.model.Students;
import com.project.model.Users;
import com.project.repository.StudentsRepository;
import com.project.repository.UsersRepository;
import com.project.ultis.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentsService {

    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    private UsersRepository usersRepository;

    // Phương thức lấy tất cả sinh viên
    public Response getAllStudents() {
        Response response = new Response();
        try {
            List<Students> list = studentsRepository.findAll();
            if (!list.isEmpty()) {
                List<StudentsDTO> listDTO = list.stream()
                        .map(Converter::convertStudentToStudentDTO)
                        .collect(Collectors.toList());

                response.setStudentsDTOList(listDTO);
                response.setStatusCode(200);
                response.setMessage("Students fetched successfully");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
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

            StudentsDTO studentsDTO = Converter.convertStudentToStudentDTO(student);

            response.setStudentsDTO(studentsDTO);
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

    public Response findStudentByNameAndExpertise(String name, String expertise){
        Response response = new Response();
        try {
            List<Students> studentsList;

            if(name != null && !name.isEmpty() && expertise != null && !expertise.isEmpty()){
                studentsList = studentsRepository.findStudentByUserFullNameAndExpertise(name, expertise);
            } else if (name != null && !name.isEmpty()) {
                studentsList = studentsRepository.findStudentByUserFullName(name);
            } else if (expertise != null && !expertise.isEmpty()) {
                studentsList = studentsRepository.findByExpertise(expertise);
            } else {
                response.setStatusCode(400);
                response.setMessage("Both name and expertise cannot be empty");
                return response;
            }

            if(!studentsList.isEmpty()){
                List<StudentsDTO> listDTO = studentsList.stream()
                        .map(Converter::convertStudentToStudentDTO)
                        .collect(Collectors.toList());

                response.setStudentsDTOList(listDTO);
                response.setStatusCode(200);
                response.setMessage("Students fetched successfully");
            }else {
                response.setStatusCode(204); // No Content
                response.setMessage("No students found");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching student: " + e.getMessage());
        }
        return response;
    }

    // Phương thức lấy sinh viên profile
    public Response getStudentProfile(String username){
        Response response = new Response();
        try {
            Optional<Users> userProfile = usersRepository.findByUsername(username);
            if(userProfile.isPresent()){
                Students studentProfile = studentsRepository.findById(userProfile.get().getId()).orElse(null);
                if (studentProfile != null) {
                    StudentsDTO studentsDTO = Converter.convertStudentToStudentDTO(studentProfile);
                    response.setStudentsDTO(studentsDTO);
                    response.setStatusCode(200);
                    response.setMessage("Successfully");
                }
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while getting user profile: " + e.getMessage());
        }
        return response;
    }
}
