package com.liwq.bookmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.annotation.NoAuth;
import com.liwq.bookmanager.annotation.RequireAdmin;
import com.liwq.bookmanager.common.Result;
import com.liwq.bookmanager.dto.*;
import com.liwq.bookmanager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @NoAuth
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            userService.register(registerDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @NoAuth
    @PostMapping("/login")
    public Result<UserDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            UserDTO userDTO = userService.login(loginDTO);
            return Result.success("登录成功", userDTO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT是无状态的，登出操作由前端处理（删除token）
        return Result.success();
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<UserDTO> getProfile(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            UserDTO userDTO = userService.getUserInfo(userId);
            return Result.success(userDTO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UpdateUserDTO updateUserDTO,
                                      HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            userService.updateUserInfo(userId, updateUserDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordDTO passwordDTO,
                                       HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            userService.updatePassword(userId, passwordDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户列表（管理员）
     */
    @RequireAdmin
    @GetMapping("/list")
    public Result<Page<UserDTO>> getUserList(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) String username,
                                             @RequestParam(required = false) String realName,
                                             @RequestParam(required = false) String role) {
        try {
            Page<UserDTO> userPage = userService.getUserList(page, size, username, realName, role);
            return Result.success(userPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户详情（管理员）
     */
    @RequireAdmin
    @GetMapping("/{id}")
    public Result<UserDTO> getUserDetail(@PathVariable Long id) {
        try {
            UserDTO userDTO = userService.getUserDetail(id);
            return Result.success(userDTO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改用户信息（管理员）
     */
    @RequireAdmin
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id,
                                   @Valid @RequestBody UpdateUserDTO updateUserDTO,
                                   @RequestParam(required = false) String role) {
        try {
            userService.updateUser(id, updateUserDTO, role);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改用户状态（管理员）
     */
    @RequireAdmin
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id,
                                         @RequestParam Integer status) {
        try {
            userService.updateUserStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
