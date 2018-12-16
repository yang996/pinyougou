package com.alibaba.springboot.controller;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Map;

/**
 * @ClassName MessageListener
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/6 16:12
 * @Version 1.0
 **/

@Component
public class MessageListener {
    @JmsListener(destination = "spring.boot.map.queue")
    public void receiveMsg(Map<String, Object> map){
        System.out.println(map);
    }
}
