package com.project.service;

import com.project.dto.*;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Class;
import com.project.model.Role;
import com.project.model.Users;
import com.project.model.Students;
import com.project.model.Mentors;
import com.project.repository.*;

import java.util.List;
import java.util.Optional;

import com.project.ultis.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Thịnh Đạt
 */
@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private MentorsRepository mentorsRepository;
    
    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SemesterRepository semesterRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Phương thức tạo user
    public Response createUser(UsersDTO registerRequest) {
        Response response = new Response();
        try {
            // Kiểm tra nếu username hoặc email đã tồn tại
            if (usersRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                throw new OurException("Username already exists");
            }
            if (usersRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                throw new OurException("Email already exists");
            }
            
            Role role = roleRepository.findByRoleName(registerRequest.getRoleString())
                    .orElseThrow(() -> new OurException("No role name: " + registerRequest.getRole().getRoleName()));
            // Mã hóa mật khẩu
            String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

            // Tạo đối tượng User mới
            Users newUser = new Users();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPassword(encodedPassword);
            newUser.setFullName(registerRequest.getFullName());
            newUser.setBirthDate(registerRequest.getBirthDate());
            newUser.setAvatar(registerRequest.getAvatar());
            newUser.setAddress(registerRequest.getAddress());
            newUser.setPhone(registerRequest.getPhone());
            newUser.setGender(registerRequest.getGender());
            newUser.setDateCreated(LocalDateTime.now());
            newUser.setRole(role);
            newUser.setAvailableStatus(AvailableStatus.ACTIVE);

            // Lưu người dùng vào database
            usersRepository.save(newUser);
            
            if (role.getRoleName().equalsIgnoreCase("STUDENT")) {
                Students student = new Students();
                student.setUser(newUser);
                student.setDateCreated(LocalDate.now());
                student.setAClass(null); // Để class_id null
                student.setGroup(null); // Để group_id null
                studentsRepository.save(student);
                newUser.setStudent(student);
                usersRepository.save(newUser);
            }
            
            if (role.getRoleName().equalsIgnoreCase("MENTOR")) {
                Mentors mentor = new Mentors();
                mentor.setUser(newUser);
                mentor.setDateCreated(LocalDate.now());
                mentorsRepository.save(mentor);
                newUser.setMentor(mentor);
                usersRepository.save(newUser);
            }
            
            if (newUser.getId() > 0) {
                UsersDTO usersDTO = userToUserDTO(newUser);
                response.setUsersDTO(usersDTO);
                response.setStatusCode(200);
                response.setMessage("User created successfully");
            }
            
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during user creation: " + e.getMessage());
        }
        return response;
    }

    // Phương thức tạo học sinh
    public Response createStudents(CreateStudentRequest request){
        Response response = new Response();
        try {
            // Kiểm tra nếu username hoặc email đã tồn tại
            if (usersRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new OurException("Username already exists");
            }
            // Kiểm tra email
            if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new OurException("Email already exists");
            }
            // Kiểm tra FullName
            if (usersRepository.findByFullName(request.getFullName()).isPresent()) {
                throw new OurException("FullName already exists");
            }
            if(usersRepository.findByPhone(request.getPhone()).isPresent()){
                throw new OurException("Phone already exists");
            }
            if(studentsRepository.findByStudentCode(request.getStudentCode()).isPresent()){
                throw new OurException("StudentCode already exists");
            }
            // Kiểm tra Class
            Class aClass = classRepository.findById(request.getAClass().getId())
                    .orElseThrow(() -> new OurException("Class not found"));
            // Kiểm tra Role
            Role role = roleRepository.findByRoleName("STUDENT")
                    .orElseThrow(() -> new OurException("No role name"));
            // Mã hóa mật khẩu
            String encodedPassword = passwordEncoder.encode(request.getPassword());

            // Tạo đối tượng User mới
            Users newUser = new Users();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(encodedPassword);
            newUser.setFullName(request.getFullName());
            newUser.setBirthDate(request.getBirthDate());
            newUser.setAvatar(request.getAvatar());
            newUser.setAddress(request.getAddress());
            newUser.setPhone(request.getPhone());
            newUser.setGender(request.getGender());
            newUser.setDateCreated(LocalDateTime.now());
            newUser.setRole(role);
            newUser.setAvailableStatus(AvailableStatus.ACTIVE);
            // Lưu người dùng vào database
            usersRepository.save(newUser);
            if (newUser.getId() > 0) {
                // Tạo đối tượng Student mới
                Students student = new Students();
                student.setUser(newUser);
                student.setExpertise(request.getExpertise());
                student.setStudentCode(request.getStudentCode());
                student.setDateCreated(LocalDate.now());
                student.setPoint(100);
                student.setAClass(aClass);
                student.setAvailableStatus(AvailableStatus.ACTIVE);
                student.setGroup(null); // Để group_id null
                studentsRepository.save(student);
                newUser.setStudent(student);
                usersRepository.save(newUser);
                if (student.getId() > 0) {
                    StudentsDTO studentsDTO = Converter.convertStudentToStudentDTO(student);
                    response.setStudentsDTO(studentsDTO);
                    response.setStatusCode(200);
                    response.setMessage("Student created successfully");
                }
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during student creation: " + e.getMessage());
        }
        return response;
    }

    // Phương thức tạo học sinh
    public Response createMentors(Response request){
        Response response = new Response();
        try {

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during user creation: " + e.getMessage());
        }
        return response;
    }

    // Phương thức trả về tất cả người dùng
    public Response getAllUser() {
        Response response = new Response();
        try {
            List<Users> list = usersRepository.findAll();
            if (!list.isEmpty()) {
                List<UsersDTO> listDTO = list.stream()
                        .map(this::userToUserDTO)
                        .collect(Collectors.toList());
                
                response.setUsersDTOList(listDTO);
                response.setStatusCode(200);
                response.setMessage("Users fetched successfully");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.getMessage();
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get all user " + e.getMessage());
        }
        
        return response;
    }

    // Phương thức tìm người dùng theo id
    public Response getUserById(Long id) {
        Response response = new Response();
        try {
            Users user = usersRepository.findById(id).orElseThrow(
                    () -> new OurException("User not found"));
            if (user != null) {
                UsersDTO userDTO = userToUserDTO(user);
                response.setUsersDTO(userDTO);
                response.setStatusCode(200);
                response.setMessage("Successfully");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage("Error occurred during get user by id " + id);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during get user by id " + id);
        }
        return response;
    }

    // Phương thức xóa người dùng theo id
    public Response deleteUser(Long id) {
        Response response = new Response();
        try {
            Users user = usersRepository.findById(id)
                    .orElseThrow(
                            () -> new OurException("User not found with id: " + id));
            
            Mentors deleteMentor = mentorsRepository.findByUser_Id(user.getId());
            if (deleteMentor != null) {
                deleteMentor.setAvailableStatus(AvailableStatus.DELETED);
                mentorsRepository.save(deleteMentor);
            }
            
            Students deleteStudent = studentsRepository.findByUser_Id(user.getId());
            if (deleteStudent != null) {
                deleteStudent.setAvailableStatus(AvailableStatus.DELETED);
                studentsRepository.save(deleteStudent);
            }
            user.setAvailableStatus(AvailableStatus.DELETED);
            usersRepository.save(user);
            response.setStatusCode(200);
            response.setMessage("User deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting user: " + id);
        }
        return response;
    }

    // Phương thức cập nhật thông tin người dùng
    public Response updateUser(Long id, Users newUser) {
        Response response = new Response();
        try {
            // Tìm kiếm người dùng theo ID
            Users user = usersRepository.findById(id)
                    .orElseThrow(() -> new OurException("User not found with id: " + id));
            user.setBirthDate(newUser.getBirthDate());
            user.setAvatar(newUser.getAvatar());
            user.setAddress(newUser.getAddress());
            user.setPhone(newUser.getPhone());
            user.setGender(newUser.getGender());
            user.setDateUpdated(LocalDateTime.now());
            usersRepository.save(user);

            // Trả về DTO và phản hồi
            UsersDTO userDTO = userToUserDTO(user);
            response.setUsersDTO(userDTO);
            response.setStatusCode(200);
            response.setMessage("User updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        
        return response;
    }

    // Phương thức lấy thông tin profile của người dùng dựa trên email
    public Response getMyProfile(String username) {
        Response response = new Response();
        try {
            Optional<Users> userProfile = usersRepository.findByUsername(username);
            if (userProfile.isPresent()) {
                UsersDTO userDTO = userToUserDTO(userProfile.get());
                response.setUsersDTO(userDTO);
                response.setStatusCode(200);
                response.setMessage("Successfully");
            } else {
                response.setStatusCode(404);
                response.setMessage("User not found");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while geting user profile: " + e.getMessage());
        }
        return response;
    }

    // Phương thức chuyển đổi từ Users sang UsersDTO
    private UsersDTO userToUserDTO(Users user) {
        UsersDTO userDTO = new UsersDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setBirthDate(user.getBirthDate());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhone(user.getPhone());
        userDTO.setGender(user.getGender());
        userDTO.setDateUpdated(user.getDateUpdated());
        userDTO.setDateCreated(user.getDateCreated());
        
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(user.getRole().getId());
        roleDTO.setRoleName(user.getRole().getRoleName());
        userDTO.setRole(roleDTO);
        return userDTO;
    }
}
