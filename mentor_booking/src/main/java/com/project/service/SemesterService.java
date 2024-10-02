package com.project.service;

import com.project.dto.ClassDTO;
import com.project.model.Class;
import com.project.dto.Response;
import com.project.dto.SemesterDTO;
import com.project.exception.OurException;
import com.project.model.Semester;
import com.project.repository.ClassRepository;
import com.project.repository.SemesterRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class SemesterService {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    @Lazy
    private ModelMapper modelMapper;

    // phương thức tạo mới Semester
    public Response createSemester(Response createRequest) {
        Response response = new Response();

        try {
            if (semesterRepository.findBySemesterName(createRequest.getSemesterDTO().getSemesterName()).isPresent()) {
                throw new OurException("Semester has already existed");
            }

            Semester semester = new Semester();
            semester.setDateCreated(LocalDateTime.now());
            semester.setSemesterName(createRequest.getSemesterDTO().getSemesterName());
            semester.setClasses(convertClassDtoListToClass(createRequest.getClassDTOList()));
            semesterRepository.save(semester);

            if (semester.getId() > 0) {
                SemesterDTO dto = modelMapper.map(semester, SemesterDTO.class);
                response.setSemesterDTO(dto);
                response.setStatusCode(201);
                response.setMessage("Semester added successfully");
            }

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during class creation: " + e.getMessage());
        }

        return response;
    }

    // phương thức tìm tất cả Semester
    public Response getAllSemesters(){
        Response response = new Response();
        try {
            List<Semester> semesterList = semesterRepository.findAll();
            List<SemesterDTO> semesterListDTO = null;
            if (semesterList != null) {
                semesterListDTO = Arrays.asList(modelMapper.map(semesterList, SemesterDTO[].class));
            }
            response.setSemesterDTOList(semesterListDTO);
            response.setStatusCode(200);
            response.setMessage("Semester fetched successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.getMessage();
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occured during get all semesters " + e.getMessage());
        }
        return response;
    }

    // phương thức tìm Semester theo Id
    public Response getSemesterById(Long id){
        Response response = new Response();
        try {
            Semester findSemester = semesterRepository.findById(id).orElse(null);
            if (findSemester != null) {
                SemesterDTO dto = modelMapper.map(findSemester, SemesterDTO.class);

                response.setSemesterDTO(dto);
                response.setStatusCode(200);
                response.setMessage("Successfully");
            }else throw new OurException("Cannot find semester");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occured during get semester " + e.getMessage());
        }
        return response;
    }

    // phương thức cập nhập mới Semester
    public Response updateSemester(Long id, Semester newSemester){
        Response response = new Response();
        try {
            Semester presentSemester = semesterRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find semester with id: "+id));
            if (semesterRepository.findBySemesterName(newSemester.getSemesterName()).isPresent()) {
                throw new OurException("Semester has already existed");
            }

            presentSemester.setSemesterName(newSemester.getSemesterName());
            presentSemester.setClasses(newSemester.getClasses());

            semesterRepository.save(presentSemester);

            SemesterDTO dto = modelMapper.map(presentSemester, SemesterDTO.class);
            response.setSemesterDTO(dto);
            response.setStatusCode(200);
            response.setMessage("Class updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating class: " + e.getMessage());
        }
        return response;
    }

    // phương thức xóa Semester
    public Response deleteSemester(Long id) {
        Response response = new Response();
        try {
            Semester deleteSemester = semesterRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find semester with id: " + id));
            semesterRepository.delete(deleteSemester);

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

    private List<Class> convertClassDtoListToClass(List<ClassDTO> lst) {
        List<Class> result = new ArrayList<>();
        for (ClassDTO dto : lst) {
            Class newClass = null;
            newClass = classRepository.findByClassName(dto.getClassName())
                    .orElseThrow(() -> new OurException("No student in the database: " + dto.getClassName()));
            if (newClass != null) {
                result.add(newClass);
            }
        }
        return result;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
