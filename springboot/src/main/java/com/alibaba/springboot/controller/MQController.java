package com.alibaba.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @ClassName MqController
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/6 20:18
 * @Version 1.0
 **/

@RequestMapping("/mq")
@RestController
public class MQController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @GetMapping("/sendSms")
    private String sendMsg(){

        HashMap<Object, Object> map = new HashMap<>();
        map.put("mobile","18777404176");
        map.put("signName","品优购");
        map.put("templateCode","SMS_150174299");
        map.put("templateParam","{\"code\":\"090410\"}");
        jmsMessagingTemplate.convertAndSend("alibaba_sms_queue",map);
        return "发送sms消息完成";
    }
}
