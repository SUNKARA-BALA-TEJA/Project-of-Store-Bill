package com.freshcart.repository;

import com.freshcart.model.User;
import com.freshcart.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    List<UserPermission> findByUser(User user);
    void deleteByUser(User user);
}
