package com.pinyougou.vo;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName Order
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/16 19:27
 * @Version 1.0
 **/
public class Order implements Serializable {
    //订单
    private TbOrder tbOrder;
    //订单明细
    private List<TbOrderItem> orderItemList;
    //商家名称
    private String sellerName;
    //商品规格
    private Map<String,String> specMap;

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Map<String, String> getSpecMap() {
        return specMap;
    }

    public void setSpecMap(Map<String, String> specMap) {
        this.specMap = specMap;
    }
}
