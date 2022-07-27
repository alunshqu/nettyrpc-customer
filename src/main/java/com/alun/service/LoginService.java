package com.alun.service;

import com.alun.annotation.RemoteInvoke;
import com.alun.spi.UserService;
import org.springframework.stereotype.Component;

@Component
public class LoginService {

    @RemoteInvoke
    private UserService userService;

    public Object getUserByName(String name) {
        return userService.getUserByName(name);
    }

}
