package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName PayController
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/15 19:21
 * @Version 1.0
 **/
@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 生成支付二维码
     *
     * @param outTradeNo 订单id
     * @return
     */
    @GetMapping("/createNative")
    public Map<String, Object> createNative(String outTradeNo) {
        //从redis中查询订单
        TbSeckillOrder seckillOrder =
                seckillOrderService.getSeckillOrderInRedisByOrderId(outTradeNo);
        if (seckillOrder != null) {
            //因为微信支付系统接收的金额不能带小数点,如果有小数点就无法支付
            String totalFee = (long) (seckillOrder.getMoney().doubleValue() * 100) + "";
            return weixinPayService.createNative(outTradeNo, totalFee);
        }
        return new HashMap<>();
    }

    /**
     * 查询支付状态
     *
     * @param outTradeNo
     * @return 支付结果
     */
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo) {
        Result result=Result.fail("支付失败");
        try {
            int count = 0;
            while (true) {
                Map<String, String> resultMap = weixinPayService.queryPayStatus(outTradeNo);
                if (resultMap == null) {
                    break;
                }
                if ("SUCCESS".equals(resultMap.get("trade_state"))){
                    result=Result.ok("支付成功");
                    //更新订单状态
                    seckillOrderService.updateOrderStatus(outTradeNo,resultMap.get("transaction_id"));
                    break;
                }
                //每隔三秒钟查询一次
                Thread.sleep(3000);
                //如果超过一分钟未支付
                if (++count > 20) {
                    resultMap=weixinPayService.closeOrder(outTradeNo);
                  //如果关闭中别支付了,那么标识为支付成功
                    if (resultMap!=null && "ORDERPAID".equals(resultMap.get("err_code"))){
                        result=Result.ok("支付成功");
                        seckillOrderService.updateOrderStatus(outTradeNo,resultMap.get("transaction_id"));
                        break;
                    }
                    //如果微信那边关闭了订单,则需要将redis中的订单删除
                    result=Result.fail("支付超时");
                    seckillOrderService.deleteRedisOrder(outTradeNo);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
