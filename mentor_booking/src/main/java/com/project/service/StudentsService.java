package com.project.service;

import com.project.dto.CreateStudentRequest;
import com.project.dto.GroupDTO;
import com.project.dto.Response;
import com.project.dto.StudentsDTO;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.*;
import com.project.model.Class;
import com.project.repository.ClassRepository;
import com.project.repository.RoleRepository;
import com.project.repository.StudentsRepository;
import com.project.repository.UsersRepository;
import com.project.security.AwsS3Service;
import com.project.ultis.Converter;
import com.project.ultis.ExcelHelper;
import com.project.ultis.Ultis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private UsersService usersService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailServiceImpl emailService;

    private static final String DEFAULT_AVATAR_URL = "https://mentor-booking-images.s3.amazonaws.com/images.jpeg";

    /**
     *  Phương thức lây tất cả danh sách học sinh
     */
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

    /**
     *  Phương thức lây tất cả danh sách học sinh theo studentID
     */
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

    /**
     *  Phương thức lây tất cả danh sách học sinh theo tên và chuyên ngành
     */
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

    /**
     *  Phương thức cập nhập sinh viên
     */
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
            // Tìm kiếm và cập nhật Students
            Students updateStudent = studentsRepository.findByUser_Id(updateUser.getId());
            if (updateStudent == null) {
                response.setStatusCode(400);
                response.setMessage("Student not found");
                return response; // Trả về phản hồi nếu không tìm thấy student
            }
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarUrl = awsS3Service.saveImageToS3(avatarFile);
                updateUser.setAvatar(avatarUrl);
                System.out.println("Avatar URL: " + avatarUrl); // Kiểm tra URL
            }
            // Kiểm tra nếu username đã thay đổi và đã tồn tại trong hệ thống
            if (!updateRequest.getUsername().equals(updateUser.getUsername())) {
                if (usersRepository.findByUsernameAndAvailableStatus(updateRequest.getUsername(), AvailableStatus.ACTIVE).isPresent()) {
                    throw new OurException("Username already exists");
                }
            }

            // Kiểm tra nếu email đã thay đổi và đã tồn tại trong hệ thống
            if (!updateRequest.getEmail().equals(updateUser.getEmail())) {
                if (usersRepository.findByEmailAndAvailableStatus(updateRequest.getEmail(), AvailableStatus.ACTIVE).isPresent()) {
                    throw new OurException("Email already exists");
                }
            }

            // Kiểm tra nếu số điện thoại đã thay đổi và đã tồn tại trong hệ thống
            if (!updateRequest.getPhone().equals(updateUser.getPhone())) {
                if (usersRepository.findByPhoneAndAvailableStatus(updateRequest.getPhone(), AvailableStatus.ACTIVE).isPresent()) {
                    throw new OurException("Phone already exists");
                }
            }
            // Kiểm tra nếu studentCode đã thay đổi và đã tồn tại trong hệ thống
            if (!updateRequest.getStudentCode().equals(updateStudent.getStudentCode())) {
                if (studentsRepository.findByStudentCodeAndAvailableStatus(updateRequest.getStudentCode(), AvailableStatus.ACTIVE).isPresent()) {
                    throw new OurException("StudentCode already exists");
                }
            }
            // Kiểm tra Class
            Class aClass = classRepository.findById(updateRequest.getAClass().getId())
                    .orElseThrow(() -> new OurException("Class not found"));
            // Cập nhật thông tin Users
            if(updateRequest.getUsername()!= null) updateUser.setUsername(updateRequest.getUsername().trim());
            if(updateRequest.getEmail()!= null) updateUser.setEmail(updateRequest.getEmail().trim());
            if(updateRequest.getFullName()!= null) updateUser.setFullName(updateRequest.getFullName().trim());
            if(updateRequest.getBirthDate()!= null) updateUser.setBirthDate(updateRequest.getBirthDate());
            if(updateRequest.getAddress()!= null) updateUser.setAddress(updateRequest.getAddress().trim());
            if(updateRequest.getPhone()!= null) updateUser.setPhone(updateRequest.getPhone().trim());
            if(updateRequest.getGender()!= null) updateUser.setGender(updateRequest.getGender());
            updateUser.setDateUpdated(LocalDateTime.now());
            updateUser.setAvailableStatus(AvailableStatus.ACTIVE);
            usersRepository.save(updateUser);

            // Tạo đối tượng Student mới
            updateStudent.setUser(updateUser);
            if(updateRequest.getExpertise()!= null) updateStudent.setExpertise(updateRequest.getExpertise().trim());
            if(updateRequest.getStudentCode()!= null) updateStudent.setStudentCode(updateRequest.getStudentCode().trim());
            updateStudent.setDateUpdated(LocalDate.now());
            if(aClass!=null) updateStudent.setAClass(aClass);
            updateStudent.setAvailableStatus(AvailableStatus.ACTIVE);
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

    /**
     *  Phương thức tìm danh sách sinh viên không có trong group
     */
    public Response findStudentsNotInGroup(Long classId){
        Response response = new Response();
        try {
            List<Students> findStudents = studentsRepository.findStudentsThatAreNotInGroup(classId, AvailableStatus.ACTIVE);
            if (findStudents != null) {
                List<StudentsDTO> findStudentsDTO = findStudents.stream()
                        .map(Converter::convertStudentToStudentDTO)
                        .collect(Collectors.toList());
                response.setStudentsDTOList(findStudentsDTO);
                response.setStatusCode(200);
                response.setMessage("Successfully");
            } else {
                throw new OurException("Cannot find group");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occured during get group " + e.getMessage());
        }
        return response;
    }

    /**
     *  Phương thức tìm danh sách sinh viên theo kì học
     */
    public Response getStudentBySemesterId(Long semesterId, String name){
        Response response = new Response();
        try{
            List<Class> findClass = classRepository.findClassBySemesterIdExcludingDeleted(semesterId, AvailableStatus.DELETED);
            if (findClass != null && !findClass.isEmpty()) {
                List<StudentsDTO> allStudent = new ArrayList<>();
                for (Class c : findClass) {
                    List<Students> studentsList;
                    if(name ==null || name.isEmpty()){
                        studentsList = studentsRepository.findStudentByClassIdExcludingDeleted(c.getId(), AvailableStatus.DELETED);
                    }else{
                        studentsList = studentsRepository.findStudentByClassIdAndFullName(c.getId(), name, AvailableStatus.DELETED);
                    }
                    if (studentsList != null && !studentsList.isEmpty()) {
                        for (Students student : studentsList) {
                            StudentsDTO studentsDTO = Converter.convertStudentToStudentDTO(student);
                            allStudent.add(studentsDTO);
                        }
                    }
                }
                response.setStudentsDTOList(allStudent);
                response.setMessage("Successfully");
                response.setStatusCode(200);
            }else{
                throw new OurException("No students found for this semester.");
            }
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occured during get student " + e.getMessage());
        }
        return response;
    }

    /**
     *  Phương thức nhập sinh viên bằng excel
     */
    public Response importStudentsFromExcel(MultipartFile file, Long semesterId){
        Response response = new Response();
        try{
            List<CreateStudentRequest> studentRequests = ExcelHelper.excelToStudents(file);
            List<String> errors = new ArrayList<>();

            for (CreateStudentRequest request : studentRequests) {
                Response createResponse = createStudentFormExcel(request, semesterId);
                if (createResponse.getStatusCode() != 200 && request.getFullName() != null) {
                        errors.add(" [" + request.getFullName() + "] ");
                }
            }
            if (!errors.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("Failed to import some students: " + String.join(", ", errors));
            } else {
                response.setStatusCode(200);
                response.setMessage("All students imported successfully");
            }

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during import: " + e.getMessage());
        }
        return response;
    }

    /**
     *  Phương thức tao sinh viên khi nhập excel
     */
    public Response createStudentFormExcel(CreateStudentRequest request, Long semesterId){
        Response response = new Response();
        try{
            // Kiểm tra nếu username hoặc email đã tồn tại
            if (usersRepository.findByUsernameAndAvailableStatus(request.getUsername(), AvailableStatus.ACTIVE).isPresent()) {
                throw new OurException("Username already exists");
            }
            // Kiểm tra email
            if (usersRepository.findByEmailAndAvailableStatus(request.getEmail(), AvailableStatus.ACTIVE).isPresent()) {
                throw new OurException("Email already exists");
            }
            if (usersRepository.findByPhoneAndAvailableStatus(request.getPhone(), AvailableStatus.ACTIVE).isPresent()) {
                throw new OurException("Phone already exists");
            }
            if (studentsRepository.findByStudentCodeAndAvailableStatus(request.getStudentCode(), AvailableStatus.ACTIVE).isPresent()) {
                throw new OurException("StudentCode already exists");
            }
            // Kiểm tra Class
            Class aClass = classRepository.findByClassNameContainingIgnoreCaseAndSemesterIdAndAvailableStatus(request.getClassName(),semesterId, AvailableStatus.ACTIVE);
            if (aClass == null) {
                throw new OurException("Class not found");
            }

            // Kiểm tra Role
            Role role = roleRepository.findByRoleName("STUDENT")
                    .orElseThrow(() -> new OurException("No role name"));

            // Mã hóa mật khẩu
            String encodedPassword = passwordEncoder.encode(emailService.sendPasswordCreateUser(request.getEmail().trim()));

            // Tạo đối tượng User mới
            Users newUser = new Users();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(encodedPassword);
            newUser.setFullName(request.getFullName());
            newUser.setBirthDate(request.getBirthDate());
            newUser.setAddress(request.getAddress());
            newUser.setPhone(request.getPhone());
            newUser.setGender(request.getGender());
            newUser.setDateCreated(LocalDateTime.now());
            newUser.setRole(role);
            newUser.setAvailableStatus(AvailableStatus.ACTIVE);
            newUser.setAvatar(DEFAULT_AVATAR_URL);

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
                student.setGroupRole(null);
                student.setAvailableStatus(AvailableStatus.ACTIVE);
                studentsRepository.save(student);

                newUser.setStudent(student);
                usersRepository.save(newUser);

                response.setStatusCode(200);
                response.setMessage("Student created successfully");
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


}
