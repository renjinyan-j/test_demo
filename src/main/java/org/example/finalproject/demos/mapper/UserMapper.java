package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.finalproject.demos.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectByUsername(@Param("username") String username);
    User selectUserRoles(@Param("userId") Long userId);

    List<User> selectAllUsers();
    User selectById(@Param("id") Long id);
    int insert(User user);
    int update(User user);
    int delete(@Param("id") Long id);

    int deleteUserRoles(@Param("userId") Long userId);
    int batchInsertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    int countByEmail(@Param("email") String email);

}
