package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/login")
@RestController
public class LoginController {
    
    @RequestMapping("/getUsername")
    public Map<String,String> login(){
        Map<String,String> map=new HashMap<>();
        String username =
                SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);
        return map;
    }
}
