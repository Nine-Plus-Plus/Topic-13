package com.project.service;

import com.project.dto.MentorsDTO;
import com.project.dto.Response;
import com.project.dto.SkillsDTO;
import com.project.exception.OurException;
import com.project.model.Mentors;
import com.project.model.Skills;
import com.project.repository.MentorsRepository;
import com.project.repository.SkillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentorsService {

    @Autowired
    private MentorsRepository mentorsRepository;

    @Autowired
    private SkillsRepository skillsRepository;

    // Phương thức tạo mentor mới
    public Response createMentor(MentorsDTO mentorsDTO) {
        Response response = new Response();
        try {
            Mentors newMentor = new Mentors();
            newMentor.setMentorCode(mentorsDTO.getMentorCode());
            newMentor.setStar(mentorsDTO.getStar());
            newMentor.setTotalTimeRemain(mentorsDTO.getTotalTimeRemain());
            newMentor.setDateCreated(mentorsDTO.getDateCreated());
            newMentor.setDateUpdated(mentorsDTO.getDateUpdated());

            // Set skills
            List<Skills> skills = skillsRepository.findAllById(
                    mentorsDTO.getSkills()
                            .stream()
                            .map(s -> s.getId())
                            .collect(Collectors.toList())
            );
            newMentor.setSkills(skills);

            mentorsRepository.save(newMentor);

            response.setStatusCode(200);
            response.setMessage("Mentor created successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while creating mentor: " + e.getMessage());
        }

        return response;
    }

    // Phương thức lấy tất cả mentors
    public Response getAllMentors() {
        Response response = new Response();
        try {
            List<Mentors> mentorsList = mentorsRepository.findAll();
            List<MentorsDTO> mentorsDTOList = mentorsList
                    .stream()
                    .map(this::mentorsToMentorsDTO)
                    .collect(Collectors.toList());

            response.setStatusCode(200);
            response.setMessage("Mentors fetched successfully");
            response.setMentorsDTOList(mentorsDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching mentors: " + e.getMessage());
        }

        return response;
    }

    // Phương thức lấy mentor theo ID
    public Response getMentorById(Long id) {
        Response response = new Response();
        try {
            Mentors mentor = mentorsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Mentor not found"));
            MentorsDTO mentorsDTO = mentorsToMentorsDTO(mentor);

            response.setStatusCode(200);
            response.setMessage("Mentor fetched successfully");
            response.setMentorsDTO(mentorsDTO);
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching mentor: " + e.getMessage());
        }

        return response;
    }

    // Phương thức cập nhật mentor
    public Response updateMentor(Long id, MentorsDTO mentorsDTO) {
        Response response = new Response();
        try {
            Mentors mentor = mentorsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Mentor not found"));

            mentor.setMentorCode(mentorsDTO.getMentorCode());
            mentor.setStar(mentorsDTO.getStar());
            mentor.setTotalTimeRemain(mentorsDTO.getTotalTimeRemain());
            mentor.setDateUpdated(mentorsDTO.getDateUpdated());

            // Set updated skills
            List<Skills> skills = skillsRepository.findAllById(
                    mentorsDTO.getSkills()
                            .stream()
                            .map(s -> s.getId())
                            .collect(Collectors.toList())
            );
            mentor.setSkills(skills);

            mentorsRepository.save(mentor);

            response.setStatusCode(200);
            response.setMessage("Mentor updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating mentor: " + e.getMessage());
        }

        return response;
    }

    // Phương thức xóa mentor theo ID
    public Response deleteMentor(Long id) {
        Response response = new Response();
        try {
            Mentors mentor = mentorsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Mentor not found"));

            mentorsRepository.delete(mentor);

            response.setStatusCode(200);
            response.setMessage("Mentor deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting mentor: " + e.getMessage());
        }

        return response;
    }

    // Phương thức chuyển đổi từ Mentors sang MentorsDTO
    private MentorsDTO mentorsToMentorsDTO(Mentors mentor) {
        MentorsDTO mentorsDTO = new MentorsDTO();
        mentorsDTO.setId(mentor.getId());
        mentorsDTO.setMentorCode(mentor.getMentorCode());
        mentorsDTO.setStar(mentor.getStar());
        mentorsDTO.setTotalTimeRemain(mentor.getTotalTimeRemain());
        mentorsDTO.setDateCreated(mentor.getDateCreated());
        mentorsDTO.setDateUpdated(mentor.getDateUpdated());

        // Map skills
        mentorsDTO.setSkills(
                mentor.getSkills().stream().map(skill -> {
                    SkillsDTO skillsDTO = new SkillsDTO();
                    skillsDTO.setId(skill.getId());
                    skillsDTO.setSkillName(skill.getSkillName());
                    skillsDTO.setSkillDescription(skill.getSkillDescription());
                    return skillsDTO;
                }).collect(Collectors.toList())
        );

        return mentorsDTO;
    }
}
