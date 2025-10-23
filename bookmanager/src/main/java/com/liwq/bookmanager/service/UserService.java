package com.liwq.bookmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.*;
import com.liwq.bookmanager.model.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     */
    UserDTO login(LoginDTO loginDTO);

    /**
     * 获取用户信息
     */
    UserDTO getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, UpdateUserDTO updateUserDTO);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, PasswordDTO passwordDTO);

    /**
     * 获取用户列表（分页）
     */
    Page<UserDTO> getUserList(Integer page, Integer size, String username, String realName, String role);

    /**
     * 获取用户详情（管理员）
     */
    UserDTO getUserDetail(Long userId);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 更新用户信息（管理员）
     */
    void updateUser(Long userId, UpdateUserDTO updateUserDTO, String role);
}
