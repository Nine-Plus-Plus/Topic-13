package com.project.controller;

import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Group;
import com.project.repository.GroupRepository;
import com.project.security.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private GroupRepository groupRepository;

    @PostMapping("user/upload")
    public ResponseEntity<String> uploadFile(
            @RequestPart("group") Long groupId,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            // Upload file lên S3 và lấy URL
            String fileUrl = awsS3Service.uploadFileToS3(file);

            // Tìm group theo groupId và status
            Group group = groupRepository.findByIdAndAvailableStatus(groupId, AvailableStatus.ACTIVE);
            if (group == null) {
                return ResponseEntity.status(404).body("Group not found or is not active.");
            }

            // Cập nhật URL file vào group và lưu lại
            group.setFileURL(fileUrl);
            groupRepository.save(group);

            return ResponseEntity.ok("File uploaded successfully. URL: " + fileUrl);
        } catch (OurException e) {
            return ResponseEntity.status(500).body("Error during upload: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("user/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        try {
            return awsS3Service.downloadFileFromS3(fileName);
        } catch (OurException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("user/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            awsS3Service.deleteFileFromS3(fileName);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (OurException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
