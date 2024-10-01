/*

 */
package com.project.controller;

import com.project.dto.Response;
import com.project.model.Class;
import com.project.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thịnh Đạt
 */
@RestController
@RequestMapping("/api")
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
    @PostMapping("/admin/create-class")
    public ResponseEntity<Response> createClass(@RequestBody Response createResponse){
        Response response = classService.CreateClass(createResponse);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/admin/get-all-class")
    public ResponseEntity<Response> getAllClass(){
        Response response = classService.getAllClasses();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/admin/get-class-by-id/{id}")
    public ResponseEntity<Response> getClassById(@PathVariable Long id){
        Response response = classService.getClassById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @PutMapping("/admin/update-class/{id}")
    public ResponseEntity<Response> updateClass(@PathVariable Long id, @RequestBody Class newClass){
        Response response = classService.updateClass(id, newClass);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @DeleteMapping("/admin/delete-class/{id}")
    public ResponseEntity<Response> deleteClass(@PathVariable Long id){
        Response response = classService.deleteClass(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
