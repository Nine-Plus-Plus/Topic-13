package com.project.service;

import com.project.dto.ClassDTO;
import com.project.dto.MentorsDTO;
import com.project.dto.Response;
import com.project.dto.SemesterDTO;
import com.project.dto.StudentsDTO;
import com.project.model.Class;
import com.project.exception.OurException;
import com.project.model.Mentors;
import com.project.model.Semester;
import com.project.model.Students;
import com.project.repository.ClassRepository;
import com.project.repository.MentorsRepository;
import com.project.repository.SemesterRepository;
import com.project.repository.StudentsRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.project.ultis.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thịnh Đạt
 */
@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private MentorsRepository mentorsRepository;

    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    @Lazy
    private ModelMapper modelMapper;

    public Response createClass(ClassDTO inputRequest) {
        Response response = new Response();
        try {
            // Kiểm tra nếu lớp đã tồn tại trong kỳ học
            if (classRepository.existsByClassNameAndSemesterId(inputRequest.getClassName(), inputRequest.getSemester().getId())) {
                throw new OurException("Class already exists in this semester");
            }
            Semester semester = semesterRepository.findById(inputRequest.getSemester().getId())
                    .orElseThrow(() -> new OurException("No semester in the database: " + inputRequest.getSemester().getId()));
            Class newClass = new Class();
            newClass.setClassName(inputRequest.getClassName());
            newClass.setDateCreated(LocalDateTime.now());
            newClass.setSemester(semester);
            classRepository.save(newClass);
            if (newClass.getId() > 0) {
                ClassDTO classDto = Converter.convertClassToClassDTO(newClass);
                response.setClassDTO(classDto);
                response.setStatusCode(200);
                response.setMessage("Class added successfully");
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

    public Response getAllClasses() {
        Response response = new Response();
        try {
            List<Class> classList = classRepository.findAll();
            if (!classList.isEmpty()) {
                List<ClassDTO> classListDTO = classList.stream()
                        .map(Converter::convertClassToClassDTO)
                        .collect(Collectors.toList());

                response.setClassDTOList(classListDTO);
                response.setStatusCode(200);
                response.setMessage("Classes fetched successfully");
            }else{
                response.setStatusCode(400);
                response.setMessage("Class not found");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get all classes " + e.getMessage());
        }
        return response;
    }

    public Response getClassesSemesterId(Long semesterId) {
        Response response = new Response();
        try {
            List<Class> classList = classRepository.findClassBySemesterId(semesterId);
            if (!classList.isEmpty()) {
                List<ClassDTO> classListDTO = classList.stream()
                        .map(Converter::convertClassToClassDTO)
                        .collect(Collectors.toList());

                response.setClassDTOList(classListDTO);
                response.setStatusCode(200);
                response.setMessage("Classes fetched successfully");
            }else{
                response.setStatusCode(400);
                response.setMessage("Class not found");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get all classes " + e.getMessage());
        }
        return response;
    }

    public Response getClassById(Long id) {
        Response response = new Response();
        try {
            Class findClass = classRepository.findById(id).orElse(null);
            if (findClass != null) {
                ClassDTO dto = Converter.convertClassToClassDTO(findClass);
                response.setClassDTO(dto);
                response.setStatusCode(200);
                response.setMessage("Successfully");
            }else throw new OurException("Cannot find class");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get class " + e.getMessage());
        }
        return response;
    }

    public Response deleteClass(Long id) {
        Response response = new Response();
        try {
            Class deletedClass = classRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find class with id: " + id));
            classRepository.delete(deletedClass);
            
            response.setStatusCode(200);
            response.setMessage("Class deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting class: " + id);
        }
        return response;
    }

    public Response updateClass(Long id, Class newClass) {
        Response response = new Response();
        try {
            Class presentClass = classRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find class with id: "+id));

            if (classRepository.findByMentorId(newClass.getMentor().getId()).isPresent()) {
                throw new OurException("Mentor has already have a class");
            }
            if (classRepository.findBySemesterId(newClass.getSemester().getId()).isPresent()) {
                throw new OurException("Class have already existed in this semester");
            }

            presentClass.setClassName(newClass.getClassName());
            presentClass.setSemester(presentClass.getSemester());
            presentClass.setMentor(newClass.getMentor());
            presentClass.setStudents(newClass.getStudents());
            
            classRepository.save(presentClass);
            
            ClassDTO dto = convertClassToClassDto(presentClass);
            response.setClassDTO(dto);
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

    private List<Students> convertStudentsDtoListToStudents(List<StudentsDTO> lst) {
        List<Students> result = new ArrayList<>();
        for (StudentsDTO dto : lst) {
            Students student = null;
            student = studentsRepository.findByStudentCode(dto.getStudentCode())
                    .orElseThrow(() -> new OurException("No student in the database: " + dto.getStudentCode()));
            if (student != null) {
                result.add(student);
            }
        }
        return result;
    }

    private ClassDTO convertClassToClassDto(Class insertClass) {
        ClassDTO classDto = new ClassDTO();
        classDto.setClassName(insertClass.getClassName());
        classDto.setSemester(this.modelMapper.map(insertClass.getSemester(), SemesterDTO.class));
        classDto.setDateCreated(insertClass.getDateCreated());
        classDto.setStudents(Arrays.asList(modelMapper.map(insertClass.getStudents(), StudentsDTO[].class)));
        classDto.setMentor(this.modelMapper.map(insertClass.getMentor(), MentorsDTO.class));
        return classDto;
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
