package com.project.service;

import com.project.dto.CreateMentorRequest;
import com.project.dto.MentorsDTO;
import com.project.dto.Response;
import com.project.dto.SkillsDTO;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Mentors;
import com.project.model.Skills;
import com.project.model.Users;
import com.project.repository.MentorsRepository;
import com.project.repository.SkillsRepository;
import com.project.repository.UsersRepository;
import com.project.ultis.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentorsService {

    @Autowired
    private MentorsRepository mentorsRepository;

    @Autowired
    private SkillsRepository skillsRepository;

    @Autowired
    private UsersRepository usersRepository;

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


    public Response updateMentor(Long userId, CreateMentorRequest updateRequest) {
        Response response = new Response();
        try {
            // Tìm kiếm user với userId và trạng thái ACTIVE
            Users updateUser = usersRepository.findByIdAndAvailableStatus(userId, AvailableStatus.ACTIVE);
            if (updateUser == null) {
                response.setStatusCode(400);
                response.setMessage("User not found");
                return response; // Trả về phản hồi nếu không tìm thấy user
            }

            // Tìm kiếm Mentor dựa trên userId
            Mentors mentorUpdate = mentorsRepository.findByUser_Id(updateUser.getId());
            if (mentorUpdate == null) {
                response.setStatusCode(400);
                response.setMessage("Mentor not found");
                return response; // Trả về phản hồi nếu không tìm thấy mentor
            }

            // Cập nhật thông tin người dùng hiện có
            updateUser.setUsername(updateRequest.getUsername());
            updateUser.setEmail(updateRequest.getEmail());
            updateUser.setFullName(updateRequest.getFullName());
            updateUser.setBirthDate(updateRequest.getBirthDate());
            updateUser.setAvatar(updateRequest.getAvatar());
            updateUser.setAddress(updateRequest.getAddress());
            updateUser.setPhone(updateRequest.getPhone());
            updateUser.setGender(updateRequest.getGender());
            updateUser.setDateUpdated(LocalDateTime.now());
            usersRepository.save(updateUser);

            // Cập nhật thông tin mentor
            mentorUpdate.setMentorCode(updateRequest.getMentorCode());
            mentorUpdate.setDateUpdated(LocalDate.now());

            // Cập nhật danh sách kỹ năng (skills)
            List<SkillsDTO> skillsListDTO = updateRequest.getSkills();
            List<Skills> skillsList = skillsListDTO.stream()
                    .map(Converter::convertSkillDTOToSkill)
                    .collect(Collectors.toList());
            mentorUpdate.setSkills(skillsList);
            mentorsRepository.save(mentorUpdate);

            MentorsDTO mentorsDTO =Converter.convertMentorToMentorDTO(mentorUpdate);
            response.setMentorsDTO(mentorsDTO);
            response.setStatusCode(200);
            response.setMessage("Mentor updated successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating mentor: " + e.getMessage());
        }
        return response;
    }


}
