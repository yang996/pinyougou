package com.pinyougou.search.activemq.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * @ClassName ItemDeleteMessageListener
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/4 20:54
 * @Version 1.0
 **/
public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();
        itemSearchService.deleteItemByGoodsId(goodsIds);
        System.out.println(" 同步删除索引库中数据完成。");
    }
}
