package com.project.controller;

import com.project.dto.MentorsDTO;
import com.project.dto.Response;
import com.project.service.MentorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MentorsController {

    @Autowired
    private MentorsService mentorsService;

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

    // Cập nhật mentor theo ID
    @PutMapping("/admin/update-mentor-by-id/{id}")
    public ResponseEntity<Response> updateMentor(@PathVariable Long id, @RequestBody MentorsDTO mentorsDTO) {
        Response response = mentorsService.updateMentor(id, mentorsDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Xóa mentor theo ID
    @DeleteMapping("/admin/delete-mentor-by-id/{id}")
    public ResponseEntity<Response> deleteMentor(@PathVariable Long id) {
        Response response = mentorsService.deleteMentor(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
