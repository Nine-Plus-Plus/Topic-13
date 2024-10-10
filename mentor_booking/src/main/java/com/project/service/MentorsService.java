package com.project.service;

import com.project.dto.MentorsDTO;
import com.project.dto.Response;
import com.project.dto.SkillsDTO;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Mentors;
import com.project.model.Skills;
import com.project.repository.MentorsRepository;
import com.project.repository.SkillsRepository;
import com.project.ultis.Converter;
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

    // Phương thức lấy tất cả mentors
    public Response getAllMentors() {
        Response response = new Response();
        try {
            List<Mentors> mentorsList = mentorsRepository.findByAvailableStatus(AvailableStatus.ACTIVE);
            List<MentorsDTO> mentorsDTOList = mentorsList
                    .stream()
                    .map(Converter::convertMentorToMentorDTO)
                    .collect(Collectors.toList());
            if(!mentorsDTOList.isEmpty()){
                response.setMentorsDTOList(mentorsDTOList);
                response.setStatusCode(200);
                response.setMessage("Mentors fetched successfully");
            }else{
                response.setMentorsDTOList(null);
                response.setMessage("No data found");
                response.setStatusCode(400);
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
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
            Mentors mentor = mentorsRepository.findByIdAndAvailableStatus(id, AvailableStatus.ACTIVE);
            if(mentor != null){
                MentorsDTO mentorsDTO = Converter.convertMentorToMentorDTO(mentor);
                response.setMentorsDTO(mentorsDTO);
                response.setStatusCode(200);
                response.setMessage("Mentor fetched successfully");
            }else{
                response.setMentorsDTO(null);
                response.setStatusCode(400);
                response.setMessage("data not found");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching mentor: " + e.getMessage());
        }

        return response;
    }
  
}
