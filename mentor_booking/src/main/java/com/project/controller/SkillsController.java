package com.project.controller;

import com.project.dto.Response;
import com.project.dto.SkillsDTO;
import com.project.service.SkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SkillsController {

    @Autowired
    SkillsService skillsService;

    // tạo skills mới
    @PostMapping("/admin/create-skill")
    public ResponseEntity<Response> createSkills(@RequestBody SkillsDTO skillsDTO){
        Response response = skillsService.createSkill(skillsDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // lấy tất cả skills
    @GetMapping("/user/get-all-skills")
    public ResponseEntity<Response> getAllSkills(){
        Response response = skillsService.getAllSkills();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    //Lấy Skills theo Id
    @GetMapping("/admin/get-skill-by-id/{id}")
    public ResponseEntity<Response> getSkillById(@PathVariable Long id){
        Response response = skillsService.getSkillById(id);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/admin/update-skill-by-id/{id}")
    public ResponseEntity<Response> updateSkill(@PathVariable Long id, @RequestBody SkillsDTO skillsDTO){
        Response response = skillsService.updateSkill(id, skillsDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete-skill/{id}")
    public ResponseEntity<Response> deleteSkill(@PathVariable Long id){
        Response response = skillsService.deleteSkill(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
