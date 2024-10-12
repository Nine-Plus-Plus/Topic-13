package com.project.ultis;

import com.project.model.*;
import com.project.model.Class;
import com.project.dto.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        if (convertClass.getSemester() != null) {
            classDTO.setSemester(convertSemesterToSemesterDTO(convertClass.getSemester()));
        }
        if (convertClass.getMentor() != null) {
            Mentors mentor = new Mentors();
            mentor.setId(convertClass.getMentor().getId());
            mentor.setMentorCode(convertClass.getMentor().getMentorCode());
            mentor.setDateCreated(convertClass.getMentor().getDateCreated());
            mentor.setStar(convertClass.getMentor().getStar());
            mentor.setTotalTimeRemain(convertClass.getMentor().getTotalTimeRemain());
            mentor.setAvailableStatus(convertClass.getMentor().getAvailableStatus());
            classDTO.setMentor(convertMentorToMentorDTO(mentor));
        }

        return classDTO;
    }

    public static SemesterDTO convertSemesterToSemesterDTO(Semester convertSemester) {
        SemesterDTO semesterDTO = new SemesterDTO();
        semesterDTO.setId(convertSemester.getId());
        semesterDTO.setSemesterName(convertSemester.getSemesterName());
        semesterDTO.setDateCreated(convertSemester.getDateCreated());
        semesterDTO.setAvailableStatus(convertSemester.getAvailableStatus());
        return semesterDTO;
    }

    public static MentorsDTO convertMentorToMentorDTO(Mentors convertMentor) {
        MentorsDTO mentorsDTO = new MentorsDTO();
        mentorsDTO.setId(convertMentor.getId());
        mentorsDTO.setMentorCode(convertMentor.getMentorCode());
        mentorsDTO.setDateCreated(convertMentor.getDateCreated());
        mentorsDTO.setDateUpdated(convertMentor.getDateUpdated());
        mentorsDTO.setStar(convertMentor.getStar());
        mentorsDTO.setTotalTimeRemain(convertMentor.getTotalTimeRemain());

        mentorsDTO.setAvailableStatus(convertMentor.getAvailableStatus());

        if (convertMentor.getAssignedClass() != null) {
            mentorsDTO.setAssignedClass(convertClassToClassDTO(convertMentor.getAssignedClass()));
        }

        if (convertMentor.getUser() != null) {
            mentorsDTO.setUser(convertUserToUserDTO(convertMentor.getUser()));
        }
        if (convertMentor.getSkills() != null) {
            List<SkillsDTO> skillsDTOList = convertMentor.getSkills().stream()
                    .map(Converter::convertSkillToSkillDTO)
                    .collect(Collectors.toList());
            mentorsDTO.setSkills(skillsDTOList);
        }
        return mentorsDTO;
    }

    public static StudentsDTO convertStudentToStudentDTO(Students convertStudent) {
        StudentsDTO studentsDTO = new StudentsDTO();
        studentsDTO.setId(convertStudent.getId());
        studentsDTO.setStudentCode(convertStudent.getStudentCode());
        studentsDTO.setExpertise(convertStudent.getExpertise());
        studentsDTO.setDateUpdated(convertStudent.getDateUpdated());
        studentsDTO.setDateCreated(convertStudent.getDateCreated());
        studentsDTO.setPoint(convertStudent.getPoint());
        if (convertStudent.getAClass() != null) {
            studentsDTO.setAClass(convertClassToClassDTO(convertStudent.getAClass()));
        }

        if (convertStudent.getUser() != null) {
            studentsDTO.setUser(convertUserToUserDTO(convertStudent.getUser()));
        }
        studentsDTO.setAvailableStatus(convertStudent.getAvailableStatus());
        return studentsDTO;
    }

    public static UsersDTO convertUserToUserDTO(Users convertUsers) {
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
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(convertUsers.getRole().getId());
        roleDTO.setRoleName(convertUsers.getRole().getRoleName());
        userDTO.setRole(roleDTO);

        userDTO.setAvailableStatus(convertUsers.getAvailableStatus());
        return userDTO;
    }

    public static SkillsDTO convertSkillToSkillDTO(Skills convertSkill) {
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

    public static TopicDTO convertTopicToTopicDTO(Topic convertTopic) {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setId(convertTopic.getId());
        topicDTO.setTopicName(convertTopic.getTopicName());
        topicDTO.setDateCreated(convertTopic.getDateCreated());
        topicDTO.setContext(convertTopic.getContext());
        topicDTO.setProblems(convertTopic.getProblems());
        topicDTO.setActor(convertTopic.getActor());
        topicDTO.setNonFunctionRequirement(convertTopic.getNonFunctionRequirement());
        topicDTO.setRequirement(convertTopic.getRequirement());

        topicDTO.setSemesterDTO(convertSemesterToSemesterDTO(convertTopic.getSemester()));
        topicDTO.setMentorsDTO(convertMentorToMentorDTO(convertTopic.getMentor()));

        topicDTO.setAvailableStatus(convertTopic.getAvailableStatus());
        return topicDTO;
    }

    public static GroupDTO convertGroupToGroupDTO(Group convertGroup) {
        GroupDTO groupDTO = new GroupDTO();
        StudentsDTO studentsDTO = new StudentsDTO();
        List<StudentsDTO> studentsListDTO = new ArrayList<>();
        groupDTO.setId(convertGroup.getId());
        groupDTO.setGroupName(convertGroup.getGroupName());
        groupDTO.setTotalPoint(convertGroup.getTotalPoint());
        groupDTO.setDateCreated(convertGroup.getDateCreated());

        for (Students student : convertGroup.getStudents()) {
            studentsDTO = convertStudentToStudentDTO(student);
            studentsDTO.setAClass(null);
            studentsDTO.setDateUpdated(null);
            studentsDTO.setDateCreated(null);
            studentsListDTO.add(studentsDTO);
        }
        groupDTO.setStudents(studentsListDTO);
        groupDTO.setClassDTO(convertClassToClassDTO(convertGroup.getAClass()));

        groupDTO.setAvailableStatus(convertGroup.getAvailableStatus());
        return groupDTO;
    }

    public static MentorScheduleDTO convertMentorScheduleToMentorScheduleDTO(MentorSchedule mentorSchedule){
        MentorScheduleDTO mentorScheduleDTO = new MentorScheduleDTO();

        mentorScheduleDTO.setId(mentorSchedule.getId());
        mentorScheduleDTO.setAvailableFrom(mentorSchedule.getAvailableFrom());
        mentorScheduleDTO.setAvailableTo(mentorSchedule.getAvailableTo());
        mentorScheduleDTO.setStatus(mentorSchedule.getStatus());
        mentorScheduleDTO.setAvailableStatus(mentorSchedule.getAvailableStatus());

        if (mentorSchedule.getMentor() != null) {
            Mentors mentor = new Mentors();
            mentor.setId(mentorSchedule.getMentor().getId());
            mentor.setMentorCode(mentorSchedule.getMentor().getMentorCode());
            mentor.setDateCreated(mentorSchedule.getMentor().getDateCreated());
            mentor.setStar(mentorSchedule.getMentor().getStar());
            mentor.setTotalTimeRemain(mentorSchedule.getMentor().getTotalTimeRemain());
            mentor.setAvailableStatus(mentorSchedule.getMentor().getAvailableStatus());
            mentorScheduleDTO.setMentor(convertMentorToMentorDTO(mentor));
        }

        return mentorScheduleDTO;
    }
}
