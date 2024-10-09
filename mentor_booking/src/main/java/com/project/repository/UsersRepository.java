package com.project.repository;

import com.project.enums.AvailableStatus;
import com.project.model.Users;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thịnh Đạt
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, Long>{
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);

    Optional<Users> findByFullName(String fullName);

    Optional<Users> findByPhone(String phone);

    // Lấy danh sách User có role là 'STUDENT'
    @Query("SELECT u FROM Users u WHERE u.role.roleName = 'STUDENT'")
    List<Users> findAllByRoleStudent();

    List<Users> findByAvailableStatus(AvailableStatus status);

    @Query("SELECT u FROM Users u WHERE u.id = :id AND u.availableStatus = :availableStatus")
    Users findByIdAndAvailableStatus(@Param("id") Long id, @Param("availableStatus") AvailableStatus status);
}
