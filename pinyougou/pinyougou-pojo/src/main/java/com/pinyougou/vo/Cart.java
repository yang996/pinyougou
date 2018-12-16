package com.pinyougou.vo;

import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName Cart
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/10 10:04
 * @Version 1.0
 **/
public class Cart implements Serializable {
    //卖家id
    private String sellerId;
    //卖家名称
    private String sellerName;
    //购物车明细
    private List<TbOrderItem> orderItemList;

    public String getSellerId() {
        return sellerId;
    }

    public Cart() {
    }

    public Cart(String sellerId, String sellerName, List<TbOrderItem> orderItemList) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.orderItemList = orderItemList;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
