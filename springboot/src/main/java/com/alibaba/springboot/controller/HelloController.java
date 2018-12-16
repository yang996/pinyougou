package com.alibaba.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HelloController
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/6 10:42
 * @Version 1.0
 **/
@RestController
public class HelloController {

    @Autowired
    private Environment environment;

    @RequestMapping("/info")
    public String info(){
        return "hello spring-boot"+environment.getProperty("url");
    }
}
