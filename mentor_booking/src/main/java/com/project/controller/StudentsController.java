package com.project.controller;

import com.project.dto.CreateStudentRequest;
import com.project.dto.Response;
import com.project.dto.StudentsDTO;
import com.project.model.Users;
import com.project.service.StudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class StudentsController {

    @Autowired
    private StudentsService studentsService;

    // Lấy tất cả sinh viên
    @GetMapping("/admin/get-all-students")
    public ResponseEntity<Response> getAllStudents() {
        Response response = studentsService.getAllStudents();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Lấy sinh viên theo ID
    @GetMapping("/admin/get-student-by-id/{id}")
    public ResponseEntity<Response> getStudentById(@PathVariable Long id) {
        Response response = studentsService.getStudentById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Lấy sinh viên theo name hoặc expertise
    @GetMapping("/user/get-student-by-name-or-expertise/")
    public ResponseEntity<Response> getStudentByNameAndExpertise(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String expertise,
            @RequestParam(required = false) Long classId) {
        Response response = studentsService.findStudentByNameAndExpertise(classId, name, expertise);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/admin/update-student/{id}")
    public ResponseEntity<Response> updateStudent(@PathVariable Long id, @RequestBody CreateStudentRequest updateStudent) {
        Response response = studentsService.updateStudent(id, updateStudent);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
