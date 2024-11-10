package com.project.security;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.exception.OurException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class AwsS3Service {
    // comment
    private final String bucketName = "mentor-booking-images";

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret-key}")
    private String awsS3SecretKey;

    @Value("${aws.s3.region}")
    private String awsRegion;

    public String saveImageToS3(MultipartFile photo){
        String s3FileName = photo.getOriginalFilename();
        String s3LocationImage = null;
        try {
            // Tạo thông tin xác thực AWS
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion("ap-southeast-2")  // Sydney region
                    .build();

            // Đọc dữ liệu từ file ảnh
            InputStream inputStream = photo.getInputStream();

            // Tạo metadata cho file
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("image/jpeg");

            // Upload file lên S3
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, inputStream, objectMetadata);
            s3Client.putObject(putObjectRequest);

            // Đóng InputStream sau khi upload
            inputStream.close();

            // Trả về URL của ảnh trên S3
            s3LocationImage = "https://" + bucketName + ".s3.amazonaws.com/" + s3FileName;
        } catch (Exception e) {
            throw new OurException("Unable to upload image to S3 bucket: " + e.getMessage());
        }

        return s3LocationImage;
    }

    // Cập nhật file S3: Xóa file cũ và upload file mới
    public String updateImageInS3(String oldImageName, MultipartFile newPhoto) {
        if (oldImageName != null && !oldImageName.isEmpty()) {
            deleteImageFromS3(oldImageName);
        }
        return saveImageToS3(newPhoto);
    }

    // Xóa file khỏi S3
    public void deleteImageFromS3(String fileName) {
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion("ap-southeast-2")
                    .build();

            s3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            throw new OurException("Unable to delete image from S3 bucket: " + e.getMessage());
        }
    }

    public String uploadFileToS3(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(awsRegion)
                    .build();

            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());

            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
            s3Client.putObject(request);

            inputStream.close();
            return "https://" + bucketName + ".s3." + awsRegion + ".amazonaws.com/" + fileName;
        } catch (Exception e) {
            throw new OurException("Unable to upload file to S3 bucket: " + e.getMessage());
        }
    }

    public ResponseEntity<InputStreamResource> downloadFileFromS3(String fileName) {
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(awsRegion)
                    .build();

            InputStream fileStream = s3Client.getObject(new GetObjectRequest(bucketName, fileName)).getObjectContent();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            throw new OurException("Unable to download file from S3 bucket: " + e.getMessage());
        }
    }
    public void deleteFileFromS3(String fileName) {
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(awsRegion)
                    .build();

            s3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            throw new OurException("Unable to delete file from S3 bucket: " + e.getMessage());
        }
    }
}
