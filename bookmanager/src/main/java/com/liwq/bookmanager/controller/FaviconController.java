package com.liwq.bookmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 处理favicon请求，避免404错误
 */
@Controller
public class FaviconController {

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        // 返回204 No Content，表示没有favicon但不是错误
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
