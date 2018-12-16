package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WeixinPayServiceImpl
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/12 20:17
 * @Version 1.0
 **/
@Service(interfaceClass = WeixinPayService.class)
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String mch_id;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notify_url;


    @Override
    public Map<String, Object> createNative(String outTradeNo, String totalFee) {
        Map<String, Object> returnMap = new HashMap<>();
        //组合要发送到微信支付系统的参数
        try {
            Map<String, String> parmMap = new HashMap<>();
            // 公众账号ID
            parmMap.put("appid", appid);
            //商户号
            parmMap.put("mch_id", mch_id);
            //随机字符串
            parmMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //签名
            parmMap.put("sign", "");
            //商品描述
            parmMap.put("body", "pyg");
            //商户订单号
            parmMap.put("out_trade_no", outTradeNo);
            //标价金额
            parmMap.put("total_fee", totalFee);
            //终端IP
            parmMap.put("spbill_create_ip", "127.0.0.1");
            //通知地址
            parmMap.put("notify_url", notify_url);
            //交易类型
            parmMap.put("trade_type", "NATIVE");

            //将参数map转换为微信支付需要的xml
            String signedXml = WXPayUtil.generateSignedXml(parmMap, partnerkey);
            System.out.println(" 发送到微信统一下单的参数为：" + signedXml);

            //创建httpclient对象并发送信息到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //微信支付返回的数据
            String content = httpClient.getContent();
            System.out.println(" 微信统一下单返回的内容为：" + content);

            //转换内容为map并设置返回结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            //业务结果
            returnMap.put("result_code", resultMap.get("result_code"));
            //二维码
            returnMap.put("code_url", resultMap.get("code_url"));
            returnMap.put("outTradeNo", outTradeNo);
            returnMap.put("totalFee", totalFee);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnMap;
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        //组合要发送到微信支付系统的参数
        try {
            Map<String, String> parmMap = new HashMap<>();
            // 公众账号ID
            parmMap.put("appid", appid);
            //商户号
            parmMap.put("mch_id", mch_id);
            //商户订单号
            parmMap.put("out_trade_no", outTradeNo);
            //随机字符串
            parmMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //签名
            parmMap.put("sign", "");
            //将参数map转换为微信支付需要的xml
            String signedXml = WXPayUtil.generateSignedXml(parmMap, partnerkey);
            //创建httpclient对象并发送信息到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //微信支付返回的数据
            String content = httpClient.getContent();
            System.out.println(" 微信查询订单返回的内容为：" + content);

            //转换内容为map并设置返回结果
            return WXPayUtil.xmlToMap(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> closeOrder(String outTradeNo) {
        //组合要发送到微信支付系统的参数
        try {
            Map<String, String> parmMap = new HashMap<>();
            // 公众账号ID
            parmMap.put("appid", appid);
            //商户号
            parmMap.put("mch_id", mch_id);
            //商户订单号
            parmMap.put("out_trade_no", outTradeNo);
            //随机字符串
            parmMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //签名
            parmMap.put("sign", "");
            //将参数map转换为微信支付需要的xml
            String signedXml = WXPayUtil.generateSignedXml(parmMap, partnerkey);
            //创建httpclient对象并发送信息到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //微信支付返回的数据
            String content = httpClient.getContent();
            System.out.println(" 微信关闭订单返回的内容为：" + content);

            //转换内容为map并设置返回结果
            return WXPayUtil.xmlToMap(content);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
