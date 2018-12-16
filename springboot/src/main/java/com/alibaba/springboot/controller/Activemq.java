package com.alibaba.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName avticemq
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/6 15:56
 * @Version 1.0
 **/
@RestController
@RequestMapping("/active")
public class Activemq {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/send")
    public String sendMsg() {

        Map<String, Object> map = new HashMap<>();
        map.put("id", 123L);
        map.put("name", " 传智播客");
        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue", map);
        return " 发送消息完成。";
    }

}
