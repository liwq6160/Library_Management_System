package com.liwq.bookmanager.annotation;

import java.lang.annotation.*;

/**
 * 不需要登录验证的注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoAuth {
}
