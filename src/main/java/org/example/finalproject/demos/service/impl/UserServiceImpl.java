package org.example.finalproject.demos.service.impl;

import org.example.finalproject.demos.mapper.UserMapper;
import org.example.finalproject.demos.pojo.User;
import org.example.finalproject.demos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    PasswordEncoder encoder;
    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User findUserWithRoles(Long id) {
        return userMapper.selectUserRoles(id);
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectAllUsers();
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(User user, List<Long> roleIds) {
        user.setPassword(encoder.encode(user.getPassword()));
        userMapper.insert(user);

        if (roleIds != null) {
            userMapper.batchInsertUserRoles(user.getId(), roleIds);
        }
    }

    @Override
    @Transactional
    public void update(User user, List<Long> roleIds) {
        userMapper.update(user);
        userMapper.deleteUserRoles(user.getId());
        userMapper.batchInsertUserRoles(user.getId(), roleIds);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userMapper.deleteUserRoles(id);
        userMapper.delete(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.countByEmail(email) > 0;
    }
}
