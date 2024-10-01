package com.project.service;

import com.project.dto.Response;
import com.project.dto.SkillsDTO;
import com.project.exception.OurException;
import com.project.model.Skills;
import com.project.repository.SkillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillsService {

    @Autowired
    private SkillsRepository skillsRepository;

    // Phương thức tạo skill mới
    public Response createSkill(SkillsDTO skillsDTO) {
        Response response = new Response();
        try {
            Skills newSkill = new Skills();
            newSkill.setSkillName(skillsDTO.getSkillName());
            newSkill.setSkillDescription(skillsDTO.getSkillDescription());

            skillsRepository.save(newSkill);

            response.setStatusCode(200);
            response.setMessage("Skill created successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while creating skill: " + e.getMessage());
        }

        return response;
    }

    // Phương thức lấy tất cả skills
    public Response getAllSkills() {
        Response response = new Response();
        try {
            List<Skills> skillsList = skillsRepository.findAll();
            List<SkillsDTO> skillsDTOList = skillsList
                    .stream()
                    .map(this::skillsToSkillsDTO)
                    .collect(Collectors.toList());

            response.setStatusCode(200);
            response.setMessage("Skills fetched successfully");
            response.setSkillsDTOList(skillsDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching skills: " + e.getMessage());
        }

        return response;
    }

    // Phương thức lấy skill theo ID
    public Response getSkillById(Long id) {
        Response response = new Response();
        try {
            Skills skill = skillsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Skill not found"));
            SkillsDTO skillsDTO = skillsToSkillsDTO(skill);

            response.setStatusCode(200);
            response.setMessage("Skill fetched successfully");
            response.setSkillsDTO(skillsDTO);
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching skill: " + e.getMessage());
        }

        return response;
    }

    // Phương thức cập nhật skill
    public Response updateSkill(Long id, SkillsDTO skillsDTO) {
        Response response = new Response();
        try {
            Skills skill = skillsRepository.findById(id).orElseThrow(() -> new OurException("Skill not found"));

            skill.setSkillName(skillsDTO.getSkillName());
            skill.setSkillDescription(skillsDTO.getSkillDescription());

            skillsRepository.save(skill);

            response.setStatusCode(200);
            response.setMessage("Skill updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating skill: " + e.getMessage());
        }

        return response;
    }

    // Phương thức xóa skill theo ID
    public Response deleteSkill(Long id) {
        Response response = new Response();
        try {
            Skills skill = skillsRepository.findById(id).orElseThrow(() -> new OurException("Skill not found"));

            skillsRepository.delete(skill);

            response.setStatusCode(200);
            response.setMessage("Skill deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting skill: " + e.getMessage());
        }

        return response;
    }

    // Phương thức chuyển đổi từ Skills sang SkillsDTO
    private SkillsDTO skillsToSkillsDTO(Skills skill) {
        SkillsDTO skillsDTO = new SkillsDTO();
        skillsDTO.setId(skill.getId());
        skillsDTO.setSkillName(skill.getSkillName());
        skillsDTO.setSkillDescription(skill.getSkillDescription());
        return skillsDTO;
    }
}
