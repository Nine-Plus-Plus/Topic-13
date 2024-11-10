package com.project.security;

import com.project.enums.AvailableStatus;
import com.project.model.Role;
import com.project.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleInitializer {

    @Bean
    public CommandLineRunner initializeRoles(RoleRepository roleRepository) {
        return args -> {
            createRoleIfNotExists(roleRepository, "ADMIN", "Administrator with full access", AvailableStatus.ACTIVE);
            createRoleIfNotExists(roleRepository, "STUDENT", "Student with limited access", AvailableStatus.ACTIVE);
            createRoleIfNotExists(roleRepository, "MENTOR", "Mentor with permission to guide students", AvailableStatus.ACTIVE);
        };
    }

    private void createRoleIfNotExists(RoleRepository roleRepository, String roleName, String description, AvailableStatus status) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            Role role = new Role();
            role.setRoleName(roleName);
            role.setRoleDescription(description);
            role.setAvailableStatus(status);
            roleRepository.save(role);
        }
    }
}
