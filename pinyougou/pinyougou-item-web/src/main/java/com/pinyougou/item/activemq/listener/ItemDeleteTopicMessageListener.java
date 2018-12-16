package com.pinyougou.item.activemq.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/**
 * 如果在运营商管理系统删除商品之后需要将商品的id集合发送到MQ,
 * 详情系统接收消息并根据商品spu id数组删除指定路径下的静态页面。
 */
public class ItemDeleteTopicMessageListener extends AbstractAdaptableMessageListener {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //1、接收商品spu id数组
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] ids = (Long[]) objectMessage.getObject();

        //2、删除指定路径下的html页面
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                File file = new File(ITEM_HTML_PATH + id + ".html");
                if (file.exists()) {
                    file.delete();
                }
            }
        }

    }
}
