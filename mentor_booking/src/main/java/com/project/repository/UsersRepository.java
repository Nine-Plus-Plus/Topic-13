package com.project.repository;

import com.project.model.Users;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    // Lấy danh sách User có role là 'STUDENT'
    @Query("SELECT u FROM Users u WHERE u.role.roleName = 'STUDENT'")
    List<Users> findAllByRoleStudent();
}
