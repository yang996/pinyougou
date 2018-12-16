package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CartController
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/10 10:12
 * @Version 1.0
 **/

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    private static final String COOKIE_CART_LIST = "PYG_CART_LIST";
    private static final Integer COOKIE_MAX_AGE = 24 * 60 * 60;


    /**
     * 添加或删除购物车商品
     *
     * @return
     */
    @CrossOrigin(origins = "http://item.pinyougou.com",allowCredentials ="true")
    @GetMapping("/addItemToCartList")
    public Result addItemToCartList(Long itemId, Integer num) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取购物车列表
            List<Cart> cartList = findCartList();
            List<Cart> newCartList = cartService.addItemToCartList(itemId, num, cartList);
            //未登录,将商品加入cookie中
            if ("anonymousUser".equals(username)) {
                String cookieCartList = JSON.toJSONString(newCartList);
                //将最新购物车列表写入cookie
                CookieUtils.setCookie(request, response, COOKIE_CART_LIST, cookieCartList, COOKIE_MAX_AGE, true);
                return Result.ok("加入购物车成功");
            } else {
                //已登录,将购物车列表写入redis
                cartService.addCartListByUsername(newCartList, username);
                return Result.ok("加入购物车成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("加入购物车失败");
    }

    /**
     * 获取用户名
     *
     * @return
     */
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return map;
    }

    /**
     * 获取购物车列表
     *
     * @return
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //读取cookie中的购物车列表
        String cartListJsonStr = CookieUtils.getCookieValue(request, COOKIE_CART_LIST, true);
        List<Cart> cookie_cartList;
        if (!StringUtils.isEmpty(cartListJsonStr)) {
            cookie_cartList = JSONArray.parseArray(cartListJsonStr, Cart.class);
        } else {
           cookie_cartList=new ArrayList<>();
        }
        //未登录,从cookie中读取购物车列表
        if ("anonymousUser".equals(username)) {
            return cookie_cartList;
        } else {
            //已登录,中redis中获取数据
            List<Cart> redis_cartList = cartService.findCartListByUsername(username);
            if (cookie_cartList.size()>0){
                //合并购物车列表
                redis_cartList=cartService.mergeCarList(cookie_cartList,redis_cartList);
                //保存最新的购物车列表到redis中
                cartService.addCartListByUsername(redis_cartList,username);
                //删除cookie中购物车列表
                CookieUtils.deleteCookie(request,response,COOKIE_CART_LIST);
            }
            return redis_cartList;
        }
    }
}
