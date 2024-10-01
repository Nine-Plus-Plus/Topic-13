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

    @Autowired
    @Lazy
    private ModelMapper modelMapperNotification;

    // Phương thức tạo notification mới
    public Response createNotification (NotificationsDTO notificationsDTO, String usernameReciver) {
        Response response = new Response();
        try {
            Users receiver = usersRepository.findByUsername(usernameReciver)
                    .orElseThrow(() -> new OurException("No receiver found"));

            Users sender = usersRepository.findByUsername(notificationsDTO.getUser().getUsername())
                    .orElseThrow(() -> new OurException("No senders found"));

            Notifications newNotification = new Notifications();
            newNotification.setType(notificationsDTO.getType());
            newNotification.setStatus(notificationsDTO.getStatus());
            newNotification.setMessage(notificationsDTO.getMessage());
            newNotification.setDateTimeCreated(notificationsDTO.getDateTimeCreated());
            newNotification.setDateTimeSent(notificationsDTO.getDateTimeSent());

            newNotification.setUser(sender);
            newNotification.setReceiver(receiver);

            notificationRepository.save(newNotification);

            if(newNotification.getId() > 0){
                response.setNotificationsDTO(convertNotificationsToNotificationsDTO(newNotification));
                response.setStatusCode(200);
                response.setMessage("Notification created successfully");
            }
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
            List<Notifications> notificationsList = notificationRepository.findAll();
            List<NotificationsDTO> notificationsDTOList = convertNotificationsListToNotificationsDTOList(notificationsList);

            response.setNotificationsDTOList(notificationsDTOList);
            response.setStatusCode(200);
            response.setMessage("Notifications fetched successfully");
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
            Notifications notifications = notificationRepository.findById(id)
                    .orElseThrow( () -> new OurException("Notification not found"));

            response.setNotificationsDTO(convertNotificationsToNotificationsDTO(notifications));
            response.setMessage("Successfully");
            response.setStatusCode(200);

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
            Notifications notification = notificationRepository.findById(id)
                    .orElseThrow( () -> new OurException("Notification not found"));

            if(notification != null){
                notificationRepository.delete(notification);
                response.setMessage("Successfully");
                response.setStatusCode(200);
            }
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
            Notifications notification = notificationRepository.findById(id)
                    .orElseThrow( () -> new OurException("Notification not found"));


                notification.setMessage(newNotification.getMessage());
                notification.setType(newNotification.getType());
                notification.setStatus(newNotification.getStatus());
                notification.setDateTimeSent(newNotification.getDateTimeSent());

                notificationRepository.save(notification);

                response.setNotificationsDTO(convertNotificationsToNotificationsDTO(notification));
                response.setMessage("Successfully");
                response.setStatusCode(200);
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occured during update notification by Id " + e.getMessage());
        }
        return response;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // Chuyển đổi từ Notifications sang NotificationsDTO
    public NotificationsDTO convertNotificationsToNotificationsDTO(Notifications notification) {
        NotificationsDTO notificationsDTO = modelMapperNotification.map(notification, NotificationsDTO.class);

        return notificationsDTO;
    }

    // Chuyển đổi danh sách Notifications sang danh sách NotificationsDTO
    public List<NotificationsDTO> convertNotificationsListToNotificationsDTOList(List<Notifications> notifications) {
        return notifications.stream()
                .map(this::convertNotificationsToNotificationsDTO)
                .collect(Collectors.toList());
    }
}
