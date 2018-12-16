package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.vo.PageResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName OrderController
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/16 15:10
 * @Version 1.0
 **/
@RequestMapping("/order")
@RestController
public class OrderController {

    @Reference
    private OrderService orderService;

    /**
     * 根据商家id查询订单信息
     *
     * @return 订单列表
     */
    @PostMapping ("/findOrderList")
    public PageResult findOrderList(@RequestBody TbOrder orders, @RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        //设置商家id
        orders.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
        return orderService.findOrderList(page, rows, orders);
    }
}
