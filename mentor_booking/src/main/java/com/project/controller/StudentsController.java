package com.project.controller;

import com.project.dto.Response;
import com.project.dto.StudentsDTO;
import com.project.service.StudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class StudentsController {

    @Autowired
    private StudentsService studentsService;

    // Tạo mới sinh viên
    @PostMapping("/admin/create-student")
    public ResponseEntity<Response> createStudent(@RequestBody StudentsDTO studentsDTO) {
        Response response = studentsService.createStudent(studentsDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

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

    // Cập nhật thông tin sinh viên
    @PutMapping("/admin/update-student-by-id/{id}")
    public ResponseEntity<Response> updateStudent(@PathVariable Long id, @RequestBody StudentsDTO studentsDTO) {
        Response response = studentsService.updateStudent(id, studentsDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Xóa sinh viên theo ID
    @DeleteMapping("/admin/delete-student-by-id/{id}")
    public ResponseEntity<Response> deleteStudent(@PathVariable Long id) {
        Response response = studentsService.deleteStudent(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
