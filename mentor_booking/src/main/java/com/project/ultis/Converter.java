package com.project.ultis;

import com.project.dto.*;
import com.project.exception.OurException;
import com.project.model.*;
import com.project.model.Class;
import com.project.repository.MentorsRepository;
import com.project.repository.UsersRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thịnh Đạt
 */
@Service
public class Converter {
    
    @Autowired
    private static MentorsRepository mentorsRepository;
    
    @Autowired
    private static UsersRepository usersRepository;

    public static ClassDTO convertClassToClassDto(Class inputClass) {
        ClassDTO classDTO = new ClassDTO();
        List<GroupDTO> groupDtoList = new ArrayList<>();
        List<StudentsDTO> StudentsDtoList = new ArrayList<>();
        MentorsDTO mentorDTO = new MentorsDTO();
        SemesterDTO semesterDTO = new SemesterDTO();

        Mentors mentor = inputClass.getMentor();
        Semester semester = inputClass.getSemester();
        try {
            classDTO.setId(inputClass.getId());
            classDTO.setClassName(inputClass.getClassName());
            mentorDTO.setId(mentor.getId());
            mentorDTO.setMentorCode(mentor.getMentorCode());
            classDTO.setMentor(mentorDTO);

            semesterDTO.setId(semester.getId());
            semesterDTO.setSemesterName(semester.getSemesterName());
            classDTO.setSemester(semesterDTO);

            for (Students student : inputClass.getStudents()) {
                StudentsDTO studentsDTO = new StudentsDTO();
                studentsDTO.setId(student.getId());
                studentsDTO.setStudentCode(student.getStudentCode());
                StudentsDtoList.add(studentsDTO);
            }
            classDTO.setStudents(StudentsDtoList);

            for (Group group : inputClass.getGroups()) {
                GroupDTO groupDto = new GroupDTO();
                groupDto.setId(group.getId());
                groupDto.setGroupName(group.getGroupName());
                groupDtoList.add(groupDto);
            }
            classDTO.setGroupDTOS(groupDtoList);
        } catch (Exception e) {
            throw new OurException(e.getMessage());
        }
        return classDTO;
    }
//
//    public static GroupDTO convertGroupToGroupDto(Group inputGroup) {
//        GroupDTO groupDto = new GroupDTO();
//        List<StudentsDTO> StudentsDtoList = new ArrayList<>();
//        try {
//            groupDto.setId(inputGroup.getId());
////            dto.setBookings(Arrays.asList(modelMapper.map(inputGroup.getBookings(), BookingDTO[].class)));
//
//            ClassDTO classDto = new ClassDTO();
//            classDto.setId(inputGroup.getAClass().getId());
//            classDto.setClassName(inputGroup.getAClass().getClassName());
//            groupDto.setClassDTO(classDto);
//            for (Students student : inputGroup.getStudents()) {
//                StudentsDTO studentsDTO = new StudentsDTO();
//                studentsDTO.setId(student.getId());
//                studentsDTO.setStudentCode(student.getStudentCode());
//                StudentsDtoList.add(studentsDTO);
//            }
//            groupDto.setStudents(StudentsDtoList);
////            dto.setProject(modelMapper.map(inputGroup.getProject(), ProjectsDTO.class));
//        } catch (Exception e) {
//            throw new OurException(e.getMessage());
//        }
//        return groupDto;
//    }
//
    public static SemesterDTO convertSemesterToSemesterDto(Semester inputSemester){
        SemesterDTO semesterDTO = new SemesterDTO();
        List<ClassDTO> classDtoList = new ArrayList<>();
        try{
            semesterDTO.setId(inputSemester.getId());
            semesterDTO.setDateCreated(inputSemester.getDateCreated());
            semesterDTO.setSemesterName(inputSemester.getSemesterName());
            
            for (Class classes : inputSemester.getClasses()) {
                ClassDTO classDTO = new ClassDTO();
                classDTO.setId(classes.getId());
                classDTO.setClassName(classes.getClassName());
                Mentors mentor = mentorsRepository.findById(classes.getMentor().getId()).get();
                MentorsDTO mentorDTO = new MentorsDTO();
                mentorDTO.setMentorCode(mentor.getMentorCode());
                classDTO.setMentor(mentorDTO);
                classDtoList.add(classDTO);
            }
            semesterDTO.setClasses(classDtoList);
            
        }catch (Exception e){
            throw new OurException(e.getMessage());
        }
        return semesterDTO;
    }
}
