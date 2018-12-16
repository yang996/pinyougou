package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List; /**
 * @ClassName CartService
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/10 15:16
 * @Version 1.0
 **/
public interface CartService {


    /**
     * 用户添加商品进购物车或者从购物车删除商品
     * @param itemId 商品sku id
     * @param num 购买数量
     * @param cartList 购物车列表
     */
    List<Cart> addItemToCartList(Long itemId, Integer num, List<Cart> cartList);

    /**
     * 根据用户名从redis中查询购物车列表
     * @param username
     * @return
     */
    List<Cart> findCartListByUsername(String username) ;

    /**
     * 将用户的购物车列表保存入redis中
     * @param cartList 购物车列表
     * @param username 用户名
     */
    void addCartListByUsername(List<Cart> cartList, String username);

    /**
     * 用户登录后合并redis和cookie中的购物车列表数据
     * @param cookie_cartList
     * @param redis_cartList
     * @return
     */
    List<Cart> mergeCarList(List<Cart> cookie_cartList, List<Cart> redis_cartList);
}
