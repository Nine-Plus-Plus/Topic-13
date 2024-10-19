package com.project.service;

import com.project.dto.NotificationsDTO;
import com.project.dto.Response;
import com.project.exception.OurException;
import com.project.model.Notifications;
import com.project.model.Users;
import com.project.repository.NotificationRepository;
import com.project.repository.UsersRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UsersRepository usersRepository;

    // Phương thức tạo notification mới
    public Response createNotification (NotificationsDTO notificationsDTO, String usernameReciver) {
        Response response = new Response();
        try {
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while creating notification: " + e.getMessage());
        }

        return response;
    }

    // Phương thức lấy tất cả notifications
    public Response getAllNotification() {
        Response response = new Response();
        try {

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching notifications: " + e.getMessage());
        }

        return response;
    }

    // Phương thức lấy notifications bằng Id
    public Response getNotificationById(Long id){
        Response response = new Response();
        try {


        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occured during get notification by Id " + e.getMessage());
        }
        return response;
    }

    // Phương thức xóa notifications bằng Id
    public Response deleteNotification(Long id){
        Response response = new Response();
        try {

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occured during delete notification by Id " + e.getMessage());
        }
        return response;
    }

    // Phương thức chỉnh sửa notifications bằng Id
    public Response updateNotification(Long id, Notifications newNotification){
        Response response = new Response();
        try {

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occured during update notification by Id " + e.getMessage());
        }
        return response;
    }
}
