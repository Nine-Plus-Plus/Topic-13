package com.project.service;

import com.project.dto.MemberGroupDTO;
import com.project.dto.Response;
import com.project.model.MemberGroup;
import com.project.repository.MemberGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MemberGroupService {

    @Autowired
    private MemberGroupRepository memberGroupRepository;

    public Response getAllMemberGroups() {
        List<MemberGroup> memberGroups = memberGroupRepository.findAll();
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("Success");
        response.setMemberGroupDTOList(memberGroups.stream().map(this::convertToDTO).toList());
        return response;
    }

    public Response getMemberGroupById(Long id) {
        Optional<MemberGroup> memberGroup = memberGroupRepository.findById(id);
        Response response = new Response();
        if (memberGroup.isPresent()) {
            response.setStatusCode(200);
            response.setMessage("Success");
            response.setMemberGroupDTO(convertToDTO(memberGroup.get()));
        } else {
            response.setStatusCode(404);
            response.setMessage("MemberGroup not found with id " + id);
        }
        return response;
    }

    public Response createMemberGroup(MemberGroupDTO memberGroupDTO) {
        MemberGroup memberGroup = new MemberGroup();
        memberGroup.setGroup(memberGroupDTO.getGroup());
        memberGroup.setStudent(memberGroupDTO.getStudent());
        memberGroup.setRole(memberGroupDTO.getRole());
        memberGroup.setStatus(memberGroupDTO.getStatus());
        memberGroup.setDateCreated(LocalDate.now());
        memberGroup.setDateUpdated(LocalDate.now());
        memberGroupRepository.save(memberGroup);

        Response response = new Response();
        response.setStatusCode(201);
        response.setMessage("MemberGroup created successfully");
        response.setMemberGroupDTO(convertToDTO(memberGroup));
        return response;
    }

    public Response updateMemberGroup(Long id, MemberGroupDTO memberGroupDTO) {
        Optional<MemberGroup> optionalMemberGroup = memberGroupRepository.findById(id);
        Response response = new Response();
        if (optionalMemberGroup.isPresent()) {
            MemberGroup memberGroup = optionalMemberGroup.get();
            memberGroup.setGroup(memberGroupDTO.getGroup());
            memberGroup.setStudent(memberGroupDTO.getStudent());
            memberGroup.setRole(memberGroupDTO.getRole());
            memberGroup.setStatus(memberGroupDTO.getStatus());
            memberGroup.setDateUpdated(LocalDate.now());
            memberGroupRepository.save(memberGroup);

            response.setStatusCode(200);
            response.setMessage("MemberGroup updated successfully");
            response.setMemberGroupDTO(convertToDTO(memberGroup));
        } else {
            response.setStatusCode(404);
            response.setMessage("MemberGroup not found with id " + id);
        }
        return response;
    }

    public Response deleteMemberGroup(Long id) {
        Optional<MemberGroup> optionalMemberGroup = memberGroupRepository.findById(id);
        Response response = new Response();
        if (optionalMemberGroup.isPresent()) {
            memberGroupRepository.deleteById(id);
            response.setStatusCode(200);
            response.setMessage("MemberGroup deleted successfully");
        } else {
            response.setStatusCode(404);
            response.setMessage("MemberGroup not found with id " + id);
        }
        return response;
    }

    private MemberGroupDTO convertToDTO(MemberGroup memberGroup) {
        MemberGroupDTO dto = new MemberGroupDTO();
        dto.setGroup(memberGroup.getGroup());
        dto.setStudent(memberGroup.getStudent());
        dto.setRole(memberGroup.getRole());
        dto.setStatus(memberGroup.getStatus());
        return dto;
    }
}