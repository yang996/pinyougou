package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName OrderController
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/16 19:12
 * @Version 1.0
 **/
@RequestMapping("/order")
@RestController
public class OrderController {
    @Reference
    private OrderService orderService;

    /**
     * 获取订单列表
     * @param searchMap 条件
     * @return
     */
    @PostMapping("/search")
    public Object getOrderList(@RequestBody Map<String,Object> searchMap) {
        try {
            String username =
                    SecurityContextHolder.getContext().getAuthentication().getName();
            Map<String, Object> resultMap = orderService.getOrderList(username, searchMap);
            //时间格式化
            String data = JSON.toJSONString(resultMap, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat);
            Object parse = JSON.parse(data);
            return parse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }


    /**
     * 取消订单
     * @param orderId 订单id
     * @return
     */
    @GetMapping("/cancelOrder")
    public Result cancelOrder(String orderId){
        try {
            Long Id=Long.parseLong(orderId);
            orderService.cancelOrder(Id);
            return Result.ok("订单已取消");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("取消订单失败");
    }

}
