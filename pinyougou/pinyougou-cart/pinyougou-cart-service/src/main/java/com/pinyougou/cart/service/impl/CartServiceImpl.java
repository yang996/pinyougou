package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CartServiceImpl
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/10 15:16
 * @Version 1.0
 **/
@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService{

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String REDIS_CART_LIST="PYG_CART_LIST";

    @Override
    public List<Cart> addItemToCartList(Long itemId, Integer num, List<Cart> cartList) {
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item==null){
            throw new RuntimeException("购买的商品不存在");
        }
        if (!"1".equals(item.getStatus())){
            throw new RuntimeException("商品状态不合法");
        }
        Cart cart = findCartBySellerId(cartList, item.getSellerId());
        //商品对应的商家不存在购物车列表
        if (cart==null){
            if (num>0){
               cart = new Cart();
                //商家id
                cart.setSellerId(item.getSellerId());
                //商家名称
                cart.setSellerName(item.getSeller());
                List<TbOrderItem> orderItemList=new ArrayList<>();
                //创建商品详细信息
                TbOrderItem orderItem = createOrderItem(item, num);
                //往商品列表中加入商品
                orderItemList.add(orderItem);
                //往购物车添加商品列表
                cart.setOrderItemList(orderItemList);
                //将购物车添加到购物车列表
                cartList.add(cart);
            }else {
                throw new RuntimeException("购买数量非法");
            }
        }else {
            //商品对应的商家存在购物车列表
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = findOrderItemToOrderItemList(item, orderItemList);
            //商品存在对应商家的订单列表中
            if (orderItem!=null){
                //将用户购买的商品数量进行叠加
                orderItem.setNum(orderItem.getNum()+num);
                //设置商品叠加后的总价
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                //如果叠加后的商品数量小于1,则将该商品从订单列表中删除
                if (orderItem.getNum()<1){
                    orderItemList.remove(orderItem);
                }
                //如果商家对应的订单列表没有商品,则将该购物车从购物车列表中删除
                if (orderItemList.size()<1){
                    cartList.remove(cart);
                }
            }else {
                if (num>0){
                    //商品不存在对应商家的订单列表中
                    orderItem=createOrderItem(item,num);
                    orderItemList.add(orderItem);
                }else {
                    throw new RuntimeException("购买数量非法");
                }
            }
        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListByUsername(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(username);
        if (cartList!=null){
            return cartList;
        }
        return new ArrayList<>();
    }

    @Override
    public void addCartListByUsername(List<Cart> cartList, String username) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(username,cartList);
    }

    @Override
    public List<Cart> mergeCarList(List<Cart> cookie_cartList, List<Cart> redis_cartList) {
        for (Cart cart : cookie_cartList) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                addItemToCartList(orderItem.getItemId(),orderItem.getNum(),redis_cartList);
            }
        }
        return redis_cartList;
    }

    /**
     * 查询商品是否存在购物车的商品列表中
     * @param item 商品
     * @param orderItemList 购物车的商品列表
     * @return
     */
    private TbOrderItem findOrderItemToOrderItemList(TbItem item, List<TbOrderItem> orderItemList) {
        if (orderItemList!=null && orderItemList.size()>0){
            for (TbOrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().equals(item.getId())){
                    return orderItem;
                }
            }
        }
        return null;
    }

    /**
     * 创建购物车订单列表中的商品信息
     * @param item 商品
     * @param num 商品数量
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setNum(num);
        orderItem.setItemId(item.getId());
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(num*orderItem.getPrice().doubleValue()));
        return orderItem;
    }

    /**
     * 根据商家id从购物车列表中查询购物车
     * @param cartList 购物车列表
     * @param sellerId 商家id
     * @return
     */
    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList!=null && cartList.size()>0){
            for (Cart cart : cartList) {
                if (cart.getSellerId().equals(sellerId)){
                    return cart;
                }
            }
        }
        return null;
    }
}
