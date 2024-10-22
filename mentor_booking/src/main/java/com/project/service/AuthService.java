package com.project.service;

import com.project.dto.Response;
import com.project.dto.UsersDTO;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Users;
import com.project.repository.UsersRepository;
import com.project.ultis.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JWTUtils jWTUltis;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Phương thức thực hiện đăng nhập cho người dùng.
     *
     * @param loginRequest Đối tượng chứa thông tin đăng nhập (username và
     *                     password).
     * @return Đối tượng Response chứa thông tin về kết quả đăng nhập, bao gồm
     * mã trạng thái và token nếu đăng nhập thành công.
     */
    public Response login(Response loginRequest) {
        Response response = new Response();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
            var user = usersRepository.findByUsernameLogin(loginRequest
                            .getUsername().trim(), AvailableStatus.ACTIVE).orElse(null);
            if(user== null){
                response.setMessage("Incorrect Username or Password");
                response.setStatusCode(400);
                return response;
            }
            var jwt = jWTUltis.generateToken(user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole().getRoleName());
            response.setExpirationTime("24 hours");
            response.setMessage("Successfully");

        } catch (OurException e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(400);
            response.setMessage("Incorrect Username or Password");
        }
        return response;
    }

    /**
     * Phương thức thay đổi mật khẩu của người dùng.
     *
     * @param email           Địa chỉ email của người dùng cần thay đổi mật khẩu.
     * @param currentPassword Mật khẩu hiện tại của người dùng.
     * @param newPassword     Mật khẩu mới mà người dùng muốn đặt.
     * @return Đối tượng Response chứa thông tin về kết quả của việc thay đổi
     * mật khẩu.
     */
    public Response changePassword(String email, String currentPassword, String newPassword) {
        Response response = new Response();
        try {
            // Tìm người dùng dựa trên username
            Users user = usersRepository.findByEmailAndAvailableStatus(email, AvailableStatus.ACTIVE)
                    .orElseThrow(() -> new OurException("User not found"));
            // Kiểm tra mật khẩu hiện tại
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new OurException("Current password is incorrect");
            }

            // Mã hóa và lưu mật khẩu mới
            user.setPassword(passwordEncoder.encode(newPassword));
            usersRepository.save(user);

            // Trả về phản hồi thành công
            response.setStatusCode(200);
            response.setMessage("Password changed successfully");

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during password change: " + e.getMessage());
        }

        return response;
    }

    public Response processOAuthPostLogin(String email, String fullName) {
        Response response = new Response();
        try {
            var user = usersRepository.findByEmailAndAvailableStatus(email, AvailableStatus.ACTIVE)
                    .orElseThrow(() -> new OurException("User not found"));
            var jwt = jWTUltis.generateToken(user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole().getRoleName());
            response.setExpirationTime("24 hours");
            response.setMessage("Successfully");

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during password change: " + e.getMessage());
        }

        return response;
    }
}
