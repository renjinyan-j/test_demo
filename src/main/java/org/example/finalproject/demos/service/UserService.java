package org.example.finalproject.demos.service;

import org.example.finalproject.demos.pojo.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User findUserWithRoles(Long id);

    List<User> findAll();
    User findById(Long id);

    void add(User user, List<Long> roleIds);
    void update(User user, List<Long> roleIds);
    void delete(Long id);

    boolean existsByEmail(String email);
}
