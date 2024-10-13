package com.project.controller;

import com.project.dto.*;
import com.project.model.Users;
import com.project.repository.SkillsRepository;
import com.project.service.MentorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MentorsController {

    @Autowired
    private MentorsService mentorsService;

    @Autowired
    private SkillsRepository skillsRepository;

    // Lấy tất cả mentors
    @GetMapping("/admin/get-all-mentors")
    public ResponseEntity<Response> getAllMentors() {
        Response response = mentorsService.getAllMentors();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Lấy mentor theo ID
    @GetMapping("/admin/get-mentor-by-id/{id}")
    public ResponseEntity<Response> getMentorById(@PathVariable Long id) {
        Response response = mentorsService.getMentorById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/admin/update-mentor/{id}")
    public ResponseEntity<Response> updateMentor(@PathVariable Long id, @RequestBody CreateMentorRequest updateMentor) {
        Response response = mentorsService.updateMentor(id, updateMentor);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/user/get-mentor-by-name-skills/")
    public ResponseEntity<Response> getMentorByNameAndSkills(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Long> skillIds) {
        Response response = mentorsService.findMentorWithNameAndSkills(name, skillIds);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
