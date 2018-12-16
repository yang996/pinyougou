package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @ClassName ItemImportMessageListener
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/4 20:11
 * @Version 1.0
 **/
public class ItemImportMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        TextMessage textMessage=(TextMessage) message;
        List<TbItem> itemList =
                JSONArray.parseArray(textMessage.getText(), TbItem.class);
        itemSearchService.importItemList(itemList);
        System.out.println(" 同步索引库数据完成。");
    }

}
