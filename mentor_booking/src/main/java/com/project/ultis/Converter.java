package com.project.ultis;

import com.project.dto.*;
import com.project.model.*;
import com.project.model.Class;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Converter {

    public static ClassDTO convertClassToClassDTO(Class convertClass) {
        ClassDTO classDTO = new ClassDTO();
        classDTO.setId(convertClass.getId());
        classDTO.setClassName(convertClass.getClassName());
        classDTO.setDateCreated(convertClass.getDateCreated());
        classDTO.setAvailableStatus(convertClass.getAvailableStatus());

        if(convertClass.getSemester() != null){
            classDTO.setSemester(convertSemesterToSemesterDTO(convertClass.getSemester()));
        }
        return classDTO;
    }

    public static SemesterDTO convertSemesterToSemesterDTO(Semester convertSemester){
        SemesterDTO semesterDTO = new SemesterDTO();
        semesterDTO.setId(convertSemester.getId());
        semesterDTO.setSemesterName(convertSemester.getSemesterName());
        semesterDTO.setDateCreated(convertSemester.getDateCreated());
        semesterDTO.setAvailableStatus(convertSemester.getAvailableStatus());
        return semesterDTO;
    }

    public static MentorsDTO convertMentorToMentorDTO(Mentors convertMentor){
        MentorsDTO mentorsDTO = new MentorsDTO();
        mentorsDTO.setId(convertMentor.getId());
        mentorsDTO.setMentorCode(convertMentor.getMentorCode());
        mentorsDTO.setDateCreated(convertMentor.getDateCreated());
        mentorsDTO.setDateUpdated(convertMentor.getDateUpdated());
        mentorsDTO.setStar(convertMentor.getStar());
        mentorsDTO.setTotalTimeRemain(convertMentor.getTotalTimeRemain());
        mentorsDTO.setAvailableStatus(convertMentor.getAvailableStatus());

        if(convertMentor.getAssignedClass() != null){
            mentorsDTO.setAssignedClass(convertClassToClassDTO(convertMentor.getAssignedClass()));
        }

        if(convertMentor.getUser() != null){
            mentorsDTO.setUser(convertUserToUserDTO(convertMentor.getUser()));
        }

        if(convertMentor.getAssignedClass() !=null){
            mentorsDTO.setAssignedClass(convertClassToClassDTO(convertMentor.getAssignedClass()));
        }

        if(convertMentor.getSkills() != null){
            List<SkillsDTO> skillsDTOList = convertMentor.getSkills().stream()
                    .map(Converter::convertSkillToSkillDTO)
                    .collect(Collectors.toList());
            mentorsDTO.setSkills(skillsDTOList);
        }
        return mentorsDTO;
    }

    public static StudentsDTO convertStudentToStudentDTO(Students convertStudent){
        StudentsDTO studentsDTO = new StudentsDTO();
        studentsDTO.setId(convertStudent.getId());
        studentsDTO.setStudentCode(convertStudent.getStudentCode());
        studentsDTO.setExpertise(convertStudent.getExpertise());
        studentsDTO.setDateUpdated(convertStudent.getDateUpdated());
        studentsDTO.setDateCreated(convertStudent.getDateCreated());
        studentsDTO.setPoint(convertStudent.getPoint());
        studentsDTO.setAvailableStatus(convertStudent.getAvailableStatus());

        if (convertStudent.getAClass() != null) {
            studentsDTO.setAClass(convertClassToClassDTO(convertStudent.getAClass()));
        }

        if (convertStudent.getUser() != null) {
            studentsDTO.setUser(convertUserToUserDTO(convertStudent.getUser()));
        }
        return studentsDTO;
    }

    public static UsersDTO convertUserToUserDTO(Users convertUsers){
        UsersDTO userDTO = new UsersDTO();
        userDTO.setId(convertUsers.getId());
        userDTO.setEmail(convertUsers.getEmail());
        userDTO.setUsername(convertUsers.getUsername());
        userDTO.setFullName(convertUsers.getFullName());
        userDTO.setBirthDate(convertUsers.getBirthDate());
        userDTO.setAvatar(convertUsers.getAvatar());
        userDTO.setAddress(convertUsers.getAddress());
        userDTO.setPhone(convertUsers.getPhone());
        userDTO.setGender(convertUsers.getGender());
        userDTO.setDateUpdated(convertUsers.getDateUpdated());
        userDTO.setDateCreated(convertUsers.getDateCreated());
        userDTO.setAvailableStatus(convertUsers.getAvailableStatus());

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(convertUsers.getRole().getId());
        roleDTO.setRoleName(convertUsers.getRole().getRoleName());
        userDTO.setRole(roleDTO);

        return userDTO;
    }

    public static SkillsDTO convertSkillToSkillDTO(Skills convertSkill){
        SkillsDTO skillsDTO = new SkillsDTO();
        skillsDTO.setId(convertSkill.getId());
        skillsDTO.setSkillName(convertSkill.getSkillName());
        skillsDTO.setSkillDescription(convertSkill.getSkillDescription());
        skillsDTO.setAvailableStatus(convertSkill.getAvailableStatus());
        return skillsDTO;
    }

    public static Skills convertSkillDTOToSkill(SkillsDTO skillsDTO) {
        Skills skill = new Skills();
        skill.setId(skillsDTO.getId()); // Nếu bạn có một ID, nếu không, có thể bỏ qua
        skill.setSkillName(skillsDTO.getSkillName());
        skill.setSkillDescription(skillsDTO.getSkillDescription());
        skill.setAvailableStatus(skillsDTO.getAvailableStatus());
        return skill;
    }
}
