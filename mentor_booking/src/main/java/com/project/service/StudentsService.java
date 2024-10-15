package com.project.service;

import com.project.dto.CreateStudentRequest;
import com.project.dto.Response;
import com.project.dto.StudentsDTO;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Class;
import com.project.model.Students;
import com.project.model.Users;
import com.project.repository.ClassRepository;
import com.project.repository.StudentsRepository;
import com.project.repository.UsersRepository;
import com.project.security.AwsS3Service;
import com.project.ultis.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentsService {

    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private AwsS3Service awsS3Service;

    // Phương thức lấy tất cả sinh viên
    public Response getAllStudents() {
        Response response = new Response();
        try {
            List<Students> list = studentsRepository.findByAvailableStatus(AvailableStatus.ACTIVE);
            List<StudentsDTO> listDTO = new ArrayList<>();
            if (!list.isEmpty()) {
                listDTO = list.stream()
                        .map(Converter::convertStudentToStudentDTO)
                        .collect(Collectors.toList());

                response.setStudentsDTOList(listDTO);
                response.setStatusCode(200);
                response.setMessage("Students fetched successfully");
            } else {
                response.setStudentsDTOList(listDTO);
                response.setStatusCode(400);
                response.setMessage("No data found");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching students: " + e.getMessage());
        }
        return response;
    }

    // Phương thức lấy sinh viên theo ID
    public Response getStudentById(Long id) {
        Response response = new Response();
        try {
            Students student = studentsRepository.findByIdAndAvailableStatus(id, AvailableStatus.ACTIVE);
            StudentsDTO studentsDTO = new StudentsDTO();
            if (student != null) {
                studentsDTO = Converter.convertStudentToStudentDTO(student);
                response.setStudentsDTO(studentsDTO);
                response.setStatusCode(200);
                response.setMessage("Student fetched successfully");
            } else {
                response.setStudentsDTO(studentsDTO);
                response.setStatusCode(400);
                response.setMessage("No data found");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching student: " + e.getMessage());
        }
        return response;
    }

    public Response findStudentByNameAndExpertise(Long classId, String name, String expertise) {
        Response response = new Response();
        try {
            List<Students> studentsList;
            List<StudentsDTO> listDTO = new ArrayList<>();
            // Kiểm tra classId và truy vấn theo name và expertise trong class
            if (classId == null) {
                response.setStudentsDTOList(null);
                response.setStatusCode(400);
                response.setMessage("Class ID cannot be null");
                return response;
            }

            // Nếu chỉ có classId (name và expertise đều là chuỗi rỗng)
            if ((name == null || name.isEmpty()) && (expertise == null || expertise.isEmpty())) {
                studentsList = studentsRepository.findStudentByClassId(classId, AvailableStatus.ACTIVE);
            }
            // Nếu có cả name và expertise
            else if (!isNullOrEmpty(name) && !isNullOrEmpty(expertise)) {
                studentsList = studentsRepository.findStudentByUserFullNameAndExpertiseAndClassId(name, expertise, AvailableStatus.ACTIVE, classId);
            }
            // Nếu chỉ có name
            else if (!isNullOrEmpty(name)) {
                studentsList = studentsRepository.findStudentByUserFullNameAndClassId(name, AvailableStatus.ACTIVE, classId);
            }
            // Nếu chỉ có expertise
            else if (!isNullOrEmpty(expertise)) {
                studentsList = studentsRepository.findByExpertiseAndClassId(expertise, AvailableStatus.ACTIVE, classId);
            } else {
                response.setStudentsDTOList(listDTO);
                response.setStatusCode(400);
                response.setMessage("Both name and expertise cannot be empty");
                return response;
            }


            if (!studentsList.isEmpty()) {
                listDTO = studentsList.stream()
                        .map(Converter::convertStudentToStudentDTO)
                        .collect(Collectors.toList());

                response.setStudentsDTOList(listDTO);
                response.setStatusCode(200);
                response.setMessage("Students fetched successfully");
            } else {
                response.setStudentsDTOList(listDTO);
                response.setStatusCode(400);
                response.setMessage("No data found");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching student: " + e.getMessage());
        }
        return response;
    }

    // Hàm kiểm tra chuỗi rỗng
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public Response updateStudent(Long userId, CreateStudentRequest updateRequest, MultipartFile avatarFile) {
        Response response = new Response();
        try {

            // Tìm kiếm user với userId và trạng thái ACTIVE
            Users updateUser = usersRepository.findByIdAndAvailableStatus(userId, AvailableStatus.ACTIVE);
            if (updateUser == null) {
                response.setStatusCode(400);
                response.setMessage("User not found");
                return response; // Trả về phản hồi nếu không tìm thấy user
            }

            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarUrl = awsS3Service.saveImageToS3(avatarFile);
                updateUser.setAvatar(avatarUrl);
                System.out.println("Avatar URL: " + avatarUrl); // Kiểm tra URL
            }

            // Kiểm tra Class
            Class aClass = classRepository.findById(updateRequest.getAClass().getId())
                    .orElseThrow(() -> new OurException("Class not found"));
            // Cập nhật thông tin Users
            updateUser.setUsername(updateRequest.getUsername());
            updateUser.setEmail(updateRequest.getEmail());
            updateUser.setFullName(updateRequest.getFullName());
            updateUser.setBirthDate(updateRequest.getBirthDate());
            updateUser.setAddress(updateRequest.getAddress());
            updateUser.setPhone(updateRequest.getPhone());
            updateUser.setGender(updateRequest.getGender());
            updateUser.setDateUpdated(LocalDateTime.now());
            updateUser.setAvailableStatus(AvailableStatus.ACTIVE);
            usersRepository.save(updateUser);
            // Tạo đối tượng Student mới
            // Tìm kiếm và cập nhật Students
            Students updateStudent = studentsRepository.findByUser_Id(updateUser.getId());
            if (updateStudent == null) {
                response.setStatusCode(400);
                response.setMessage("Student not found");
                return response; // Trả về phản hồi nếu không tìm thấy student
            }
            updateStudent.setUser(updateUser);
            updateStudent.setExpertise(updateRequest.getExpertise());
            updateStudent.setStudentCode(updateRequest.getStudentCode());
            updateStudent.setDateUpdated(LocalDate.now());
            updateStudent.setAClass(aClass);
            updateStudent.setAvailableStatus(AvailableStatus.ACTIVE);
            updateStudent.setGroup(null); // Đặt group là null nếu cần thiết
            studentsRepository.save(updateStudent);

            // Chuyển đổi đối tượng student sang DTO
            StudentsDTO studentsDTO = Converter.convertStudentToStudentDTO(updateStudent);
            response.setStudentsDTO(studentsDTO);
            response.setStatusCode(200);
            response.setMessage("Student updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching student: " + e.getMessage());
        }
        return response;
    }
}
