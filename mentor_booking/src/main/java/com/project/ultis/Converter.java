package com.project.ultis;

import com.project.dto.NotificationsDTO;
import com.project.dto.RoleDTO;
import com.project.dto.UsersDTO;
import com.project.model.Notifications;
import com.project.model.Users;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Converter {

    @Autowired
    private static ModelMapper modelMapper;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // Chuyển đổi từ Users sang UsersDTO
    public static UsersDTO convertUsersToUsersDTO(Users user) {
        UsersDTO userDTO = modelMapper.map(user, UsersDTO.class);

        // Nếu cần ánh xạ riêng RoleDTO, có thể làm như sau:
        RoleDTO roleDTO = modelMapper.map(user.getRole(), RoleDTO.class);
        userDTO.setRole(roleDTO);

        return userDTO;
    }

    // Chuyển đổi từ Notifications sang NotificationsDTO
    public static NotificationsDTO convertNotificationsToNotificationsDTO(Notifications notification) {
        NotificationsDTO notificationsDTO = modelMapper.map(notification, NotificationsDTO.class);

        return notificationsDTO;
    }

    // Chuyển đổi danh sách Notifications sang danh sách NotificationsDTO
    public static List<NotificationsDTO> convertNotificationsListToNotificationsDTOList(List<Notifications> notifications) {
        return notifications.stream()
                .map(Converter::convertNotificationsToNotificationsDTO) // Gọi phương thức chuyển đổi cho từng phần tử
                .collect(Collectors.toList()); // Tập hợp các phần tử thành một danh sách
    }
}
