package com.project.controller;

import com.project.dto.MemberGroupDTO;
import com.project.dto.Response;
import com.project.service.MemberGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MemberGroupController {

    @Autowired
    private MemberGroupService memberGroupService;

    @PostMapping("/admin/create-member-group")
    public ResponseEntity<Response> createMemberGroup(@RequestBody MemberGroupDTO createRequest) {
        Response response = memberGroupService.createMemberGroup(createRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/admin/get-all-member-groups")
    public ResponseEntity<Response> getAllMemberGroups() {
        Response response = memberGroupService.getAllMemberGroups();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/admin/get-member-group-by-id/{id}")
    public ResponseEntity<Response> getMemberGroupById(@PathVariable Long id) {
        Response response = memberGroupService.getMemberGroupById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/admin/update-member-group/{id}")
    public ResponseEntity<Response> updateMemberGroup(@PathVariable Long id, @RequestBody MemberGroupDTO updateRequest) {
        Response response = memberGroupService.updateMemberGroup(id, updateRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete-member-group/{id}")
    public ResponseEntity<Response> deleteMemberGroup(@PathVariable Long id) {
        Response response = memberGroupService.deleteMemberGroup(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}