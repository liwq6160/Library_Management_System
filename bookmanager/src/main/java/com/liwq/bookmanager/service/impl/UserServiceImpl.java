package com.liwq.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.*;
import com.liwq.bookmanager.mapper.UserMapper;
import com.liwq.bookmanager.model.User;
import com.liwq.bookmanager.service.UserService;
import com.liwq.bookmanager.util.JwtUtil;
import com.liwq.bookmanager.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        User existUser = userMapper.selectOne(wrapper);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (StringUtils.hasText(registerDTO.getEmail())) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, registerDTO.getEmail());
            existUser = userMapper.selectOne(wrapper);
            if (existUser != null) {
                throw new RuntimeException("邮箱已被注册");
            }
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(PasswordUtil.encryptPassword(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setRole("user"); // 默认普通用户
        user.setStatus(1); // 默认启用

        userMapper.insert(user);
    }

    @Override
    public UserDTO login(LoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 构建返回结果
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setToken(token);

        return userDTO;
    }

    @Override
    public UserDTO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public void updateUserInfo(Long userId, UpdateUserDTO updateUserDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查邮箱是否已被其他用户使用
        if (StringUtils.hasText(updateUserDTO.getEmail())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, updateUserDTO.getEmail());
            wrapper.ne(User::getId, userId);
            User existUser = userMapper.selectOne(wrapper);
            if (existUser != null) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
        }

        // 更新用户信息
        if (StringUtils.hasText(updateUserDTO.getRealName())) {
            user.setRealName(updateUserDTO.getRealName());
        }
        if (StringUtils.hasText(updateUserDTO.getPhone())) {
            user.setPhone(updateUserDTO.getPhone());
        }
        if (StringUtils.hasText(updateUserDTO.getEmail())) {
            user.setEmail(updateUserDTO.getEmail());
        }

        userMapper.updateById(user);
    }

    @Override
    public void updatePassword(Long userId, PasswordDTO passwordDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证原密码
        if (!PasswordUtil.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        user.setPassword(PasswordUtil.encryptPassword(passwordDTO.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    public Page<UserDTO> getUserList(Integer page, Integer size, String username, String realName, String role) {
        Page<User> userPage = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(User::getRealName, realName);
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }

        wrapper.orderByDesc(User::getCreateTime);
        userMapper.selectPage(userPage, wrapper);

        // 转换为DTO
        Page<UserDTO> dtoPage = new Page<>(page, size, userPage.getTotal());
        List<UserDTO> dtoList = userPage.getRecords().stream().map(user -> {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(user, dto);
            return dto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public UserDTO getUserDetail(Long userId) {
        return getUserInfo(userId);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void updateUser(Long userId, UpdateUserDTO updateUserDTO, String role) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查邮箱是否已被其他用户使用
        if (StringUtils.hasText(updateUserDTO.getEmail())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, updateUserDTO.getEmail());
            wrapper.ne(User::getId, userId);
            User existUser = userMapper.selectOne(wrapper);
            if (existUser != null) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
        }

        // 更新用户信息
        if (StringUtils.hasText(updateUserDTO.getRealName())) {
            user.setRealName(updateUserDTO.getRealName());
        }
        if (StringUtils.hasText(updateUserDTO.getPhone())) {
            user.setPhone(updateUserDTO.getPhone());
        }
        if (StringUtils.hasText(updateUserDTO.getEmail())) {
            user.setEmail(updateUserDTO.getEmail());
        }
        if (StringUtils.hasText(role)) {
            user.setRole(role);
        }

        userMapper.updateById(user);
    }
}
