
package com.project.controller;

import com.project.dto.CreateMentorRequest;
import com.project.dto.CreateStudentRequest;
import com.project.dto.Response;
import com.project.dto.UsersDTO;
import com.project.model.Users;
import com.project.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UsersController {
    @Autowired
    private UsersService userService;
    
    @PostMapping("/admin/create-user")
    public ResponseEntity<Response> createUser(@RequestBody UsersDTO createRes){
        Response response = userService.createUser(createRes);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/admin/create-student")
    public ResponseEntity<Response> createStudentRequestResponseEntity(@RequestBody CreateStudentRequest createStudentRequest){
        Response response = userService.createStudents(createStudentRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/admin/create-mentor")
    public ResponseEntity<Response> createMentorRequestResponseEntity(@RequestBody CreateMentorRequest createMentorRequest){
        Response response = userService.createMentors(createMentorRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/admin/get-all-users")
    public ResponseEntity<Response> getAllUsers(){
        Response response = userService.getAllUser();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/admin/get-user-by-id/{id}")
    public ResponseEntity<Response> getUserById(@PathVariable Long id){
        Response response = userService.getUserById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @PutMapping("/admin/update-user/{id}")
    public ResponseEntity<Response> updateUser(@PathVariable Long id, @RequestBody Users user) {
        Response response = userService.updateUser(id, user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @DeleteMapping("/admin/delete-user/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
        Response response = userService.deleteUser(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/user/get-my-profile")
    public ResponseEntity<Response> getMyProfile() {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Response response = userService.getMyProfile(username);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}