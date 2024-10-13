package com.project.service;

import com.project.dto.*;
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
import java.util.ArrayList;
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
        List<MentorsDTO> mentorsDTOList = new ArrayList<>();
        try {
            List<Mentors> mentorsList = mentorsRepository.findByAvailableStatus(AvailableStatus.ACTIVE);
            mentorsDTOList = mentorsList
                    .stream()
                    .map(Converter::convertMentorToMentorDTO)
                    .collect(Collectors.toList());
            if (!mentorsDTOList.isEmpty()) {
                response.setMentorsDTOList(mentorsDTOList);
                response.setStatusCode(200);
                response.setMessage("Mentors fetched successfully");
            } else {
                response.setMentorsDTOList(mentorsDTOList);
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
        MentorsDTO mentorsDTO = new MentorsDTO();
        try {
            Mentors mentor = mentorsRepository.findByIdAndAvailableStatus(id, AvailableStatus.ACTIVE);
            if (mentor != null) {
                mentorsDTO = Converter.convertMentorToMentorDTO(mentor);
                response.setMentorsDTO(mentorsDTO);
                response.setStatusCode(200);
                response.setMessage("Mentor fetched successfully");
            } else {
                response.setMentorsDTO(mentorsDTO);
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
            mentorUpdate.setStar(updateRequest.getStar());
            mentorUpdate.setTotalTimeRemain(updateRequest.getTotalTimeRemain());

            // Cập nhật danh sách kỹ năng (skills)
            List<SkillsDTO> skillsListDTO = updateRequest.getSkills();
            List<Skills> skillsList = skillsListDTO.stream()
                    .map(Converter::convertSkillDTOToSkill)
                    .collect(Collectors.toList());
            mentorUpdate.setSkills(skillsList);
            mentorsRepository.save(mentorUpdate);

            MentorsDTO mentorsDTO = Converter.convertMentorToMentorDTO(mentorUpdate);
            response.setMentorsDTO(mentorsDTO);
            response.setStatusCode(200);
            response.setMessage("Mentor updated successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating mentor: " + e.getMessage());
        }
        return response;
    }

    // Hàm kiểm tra chuỗi rỗng
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public Response findMentorWithNameAndSkills(String name, List<Long> skillIds) {
        Response response = new Response();
        try {
            List<MentorsDTO> mentorsDTOList = new ArrayList<>();

            // Nếu cả name và skills đều rỗng
            if (isNullOrEmpty(name) && (skillIds == null || skillIds.isEmpty())) {
                List<Mentors> mentorsList = mentorsRepository.findByAvailableStatus(AvailableStatus.ACTIVE);
                mentorsDTOList = mentorsList
                        .stream()
                        .map(Converter::convertMentorToMentorDTO)
                        .collect(Collectors.toList());
            }
            // Nếu có cả name và skills
            else if (!isNullOrEmpty(name) && skillIds != null && !skillIds.isEmpty()) {
                // Tìm các đối tượng Skills dựa trên skillIds
                List<Skills> skillsList = skillsRepository.findAllById(skillIds);
                if (skillsList.isEmpty()) {
                    response.setStatusCode(400);
                    response.setMessage("Skills not found.");
                    return response;
                }
                List<Mentors> mentorsList = mentorsRepository.findByNameAndSkills(name, skillsList, AvailableStatus.ACTIVE);
                mentorsDTOList = mentorsList
                        .stream()
                        .map(Converter::convertMentorToMentorDTO)
                        .collect(Collectors.toList());
            }
            // Nếu chỉ có name
            else if (!isNullOrEmpty(name)) {
                List<Mentors> mentorsList = mentorsRepository.findByName(name, AvailableStatus.ACTIVE);
                mentorsDTOList = mentorsList
                        .stream()
                        .map(Converter::convertMentorToMentorDTO)
                        .collect(Collectors.toList());
            }
            // Nếu chỉ có skills
            else if (skillIds != null && !skillIds.isEmpty()) {
                // Tìm các đối tượng Skills dựa trên skillIds
                List<Skills> skillsList = skillsRepository.findAllById(skillIds);
                if (skillsList.isEmpty()) {
                    response.setStatusCode(400);
                    response.setMessage("Skills not found.");
                    return response;
                }
                List<Mentors> mentorsList = mentorsRepository.findBySkills(skillsList, AvailableStatus.ACTIVE);
                mentorsDTOList = mentorsList
                        .stream()
                        .map(Converter::convertMentorToMentorDTO)
                        .collect(Collectors.toList());
            }
            if (mentorsDTOList.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("No mentors found.");
            } else {
                response.setStatusCode(200);
                response.setMentorsDTOList(mentorsDTOList);
                response.setMessage("Mentors found successfully.");
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

    public UsersDTO getMentorInformation(Long mentorId) {
        Response response = new Response();

        UsersDTO usersDTO = new UsersDTO();
        Mentors mentor = mentorsRepository.findByIdAndAvailableStatus(mentorId, AvailableStatus.ACTIVE);
        if (mentor == null) {
            return usersDTO;
        }
        usersDTO.setId(mentor.getUser().getId());
        usersDTO.setFullName(mentor.getUser().getFullName());
        usersDTO.setAvatar(mentor.getUser().getAvatar());
        usersDTO.setAddress(mentor.getUser().getAddress());
        usersDTO.setBirthDate(mentor.getUser().getBirthDate());
        usersDTO.setGender(mentor.getUser().getGender());
        usersDTO.setEmail(mentor.getUser().getEmail());

        response.setUsersDTO(usersDTO);
        return usersDTO;
    }

    public List<SkillsDTO> getSkillsByMentor(Long mentorId){
        List<SkillsDTO> skillsDTOList = new ArrayList<>();
        List<Skills> skillsList = mentorsRepository.findByIdAndAvailableStatus(mentorId,AvailableStatus.ACTIVE).getSkills();
        skillsDTOList = skillsList
                .stream()
                .map(Converter::convertSkillToSkillDTO)
                .collect(Collectors.toList());
        return skillsDTOList;
    }
}
