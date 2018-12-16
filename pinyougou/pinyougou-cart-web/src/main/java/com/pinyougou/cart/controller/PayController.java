package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName PayController
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/12 19:45
 * @Version 1.0
 **/
@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private OrderService orderService;
    @Reference
    private WeixinPayService weixinPayService;



    /**
     * 接收支付日志id(交易号)，查询支付日志并获取支付总金额，
     * 调用支付系统的统一下单接口生成微信订单返回前端需要的数据
     * @param outTradeNo
     * @return
     */
    @GetMapping("/createNative")
    public Map<String,Object> createNative(String outTradeNo){
        //查询支付日志信息
        TbPayLog payLog=orderService.findPayLogByOutTradeNO(outTradeNo);
        if (!StringUtils.isEmpty(payLog)){
            //到支付系统进行提交订单并返回支付地址
            return weixinPayService.createNative(outTradeNo,payLog.getTotalFee().toString());
        }
        return new HashMap<>();
    }

    /**
     * 查询支付状态
     * @param outTradeNo 支付日志id
     * @return  支付结果
     */
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo){
        try {
            int count=0;
            while (true){
                Map<String,String> resultMap=weixinPayService.queryPayStatus(outTradeNo);
                if (StringUtils.isEmpty(resultMap)){
                    break;
                }
                //支付成功
                if ("SUCCESS".equals(resultMap.get("trade_state"))){
                    orderService.updateOrderStatus(outTradeNo,resultMap.get("transaction_id"));
                    return Result.ok("支付成功");
                }
                //每三秒查询一次支付状态
                Thread.sleep(3000L);
                //如果超过三分钟用户都未支付,则重新生成二维码
                if (++count>60){
                    return Result.fail("二维码重新生成");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.fail("支付失败");
    }
}
