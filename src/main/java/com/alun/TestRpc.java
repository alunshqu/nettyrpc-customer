package com.alun;

import com.alun.service.LoginService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestRpc {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.alun");
        LoginService loginService = context.getBean(LoginService.class);
        loginService.getUserByName("张三");
        /*new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            loginService.getUserByName("张三" + System.currentTimeMillis());
        }, 0, 2, TimeUnit.SECONDS);*/
    }
}
