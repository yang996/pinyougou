package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Order;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

@Service(interfaceClass = OrderService.class, timeout = 30000)
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayLogMapper payLogMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private IdWorker idWorker;

    private static final String REDIS_CART_LIST = "PYG_CART_LIST";

    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public String addOrder(TbOrder order) {
        //支付日志id,若非微信支付可以为空
        String outTradeNo = "";
        //1、查询redis中的购物车列表
        List<Cart> cartList =
                (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(order.getUserId());
        //本次应该支付总金额
        double totalFee = 0.0;
        //本次交易的订单id集合
        String orderIds = "";
        if (cartList != null && cartList.size() > 0) {
            // 2、遍历每一个购物车对应生成一个订单
            for (Cart cart : cartList) {
                Long orderId = idWorker.nextId();
                order.setOrderId(orderId);
                //未付款
                order.setStatus("1");
                order.setCreateTime(new Date());
                order.setUpdateTime(new Date());
                order.setSellerId(cart.getSellerId());
                List<TbOrderItem> orderItemList = cart.getOrderItemList();

                //本笔订单的支付总金额
                double payment = 0.0;
                // 2.1、遍历每一个商家里面买的那些商品生成订单明细
                for (TbOrderItem orderItem : orderItemList) {
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(orderId);
                    payment += orderItem.getTotalFee().doubleValue();
                    orderItemMapper.insertSelective(orderItem);
                }
                order.setPayment(new BigDecimal(payment));
                orderMapper.insertSelective(order);
                //记录订单id
                if (orderIds.length() > 0) {
                    orderIds += "," + order.getOrderId();
                } else {
                    orderIds += orderId + "";
                }
                totalFee += payment;
            }
            //3、如果是微信支付则需要生成支付日志
            if ("1".equals(order.getPaymentType())) {
                outTradeNo = idWorker.nextId() + "";
                TbPayLog payLog = new TbPayLog();
                payLog.setOutTradeNo(outTradeNo);
                //未支付
                payLog.setTradeState("0");
                payLog.setUserId(order.getUserId());
                payLog.setCreateTime(new Date());
                //总金额:取整,单位为分
                payLog.setTotalFee((long) (totalFee * 100));
                //订单id集合
                payLog.setOrderList(orderIds);
                payLogMapper.insertSelective(payLog);
            }
            // 4、删除redis中的购物车列表
            redisTemplate.boundHashOps(REDIS_CART_LIST).delete(order.getUserId());
        }
        // 5、如果是微信支付则返回支付日志id，如果是货到付款则返回空字符串
        return outTradeNo;
    }

    @Override
    public TbPayLog findPayLogByOutTradeNO(String outTradeNo) {
        return payLogMapper.selectByPrimaryKey(outTradeNo);
    }

    @Override
    public void updateOrderStatus(String outTradeNo, String transaction_id) {
        TbPayLog payLog = findPayLogByOutTradeNO(outTradeNo);
        payLog.setOutTradeNo(outTradeNo);
        payLog.setTradeState("1");
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);
        payLogMapper.updateByPrimaryKeySelective(payLog);


        //更新支付日志中对应的每一笔订单的支付状态
        //根据支付日志id查询订单列表id
        String[] orderList = payLog.getOrderList().split(",");
        TbOrder order = new TbOrder();
        order.setPaymentTime(payLog.getPayTime());
        order.setStatus("2");
        Example example = new Example(TbOrder.class);
        example.createCriteria().andIn("orderId", Arrays.asList(orderList));
        orderMapper.updateByExampleSelective(order, example);
    }

    @Override
    public PageResult findOrderList(Integer page, Integer rows, TbOrder orders) {
        PageHelper.startPage(page, rows);
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(orders.getStatus())) {
            criteria.andEqualTo("status", orders.getStatus());
        }
        // if (!StringUtils.isEmpty(goods.getGoodsName())) {
        //     criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        // }
        if (!StringUtils.isEmpty(orders.getSellerId())) {
            criteria.andEqualTo("sellerId", orders.getSellerId());
        }
        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Map<String, Object> getOrderList(String userId, Map<String, Object> searchMap) {
        //封装当前页的订单数据
        List<Order> orderList = new ArrayList<>();
        //封装分页结果返回,包含当前页的数据
        Map<String, Object> resultMap = new HashMap<>();
        //当前页
        int pageNo = 1;
        //页大小
        int pageSize = 20;
        if (searchMap.get("pageNo") != null) {
            pageNo = Integer.parseInt(searchMap.get("pageNo").toString());
        }
        if (searchMap.get("pageSize") != null) {
            pageSize = Integer.parseInt(searchMap.get("pageSize").toString());
        }
        //设置分页查询
        PageHelper.startPage(pageNo, pageSize);
        //查询订单信息
        Example example = new Example(TbOrder.class);
        example.createCriteria().andEqualTo("userId", userId);
        example.orderBy("createTime").desc();
        //获取数据库中的订单
        List<TbOrder> tbOrderList = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(tbOrderList);
        //当前页数据列表
        List<TbOrder> pageInfoList = pageInfo.getList();
        if (pageInfoList != null && pageInfoList.size() > 0) {
            for (TbOrder tbOrder : pageInfoList) {
                Order order = new Order();
                //查询订单明细
                Example example1 = new Example(TbOrderItem.class);
                example1.createCriteria().andEqualTo("orderId", tbOrder.getOrderId());
                List<TbOrderItem> orderItemList = orderItemMapper.selectByExample(example1);
                //存储订单明细的规格                                      
                //商品订单明细和商品sku的规格的对应关系
                Map<String, String> specMap = new HashMap<>();
                for (TbOrderItem orderItem : orderItemList) {
                    //Map<String,String> specMap=new HashMap<>();
                    //查询商品sku,目的就是为了获取商品sku对应的规格
                    TbItem item = itemMapper.selectByPrimaryKey(orderItem.getItemId());
                    specMap.put(orderItem.getId().toString(), item.getSpec());
                    //specList.add(specMap);
                }
                TbSeller seller = sellerMapper.selectByPrimaryKey(tbOrder.getSellerId());
                order.setTbOrder(tbOrder);
                order.setOrderItemList(orderItemList);
                //商家名称
                order.setSellerName(seller.getName());
                //商品规格
                order.setSpecMap(specMap);
                //添加order
                orderList.add(order);
            }
        }
        //总记录数
        resultMap.put("totalPages", pageInfo.getPages());
        //当前页订单信息
        resultMap.put("orderList", orderList);
        return resultMap;
    }

    @Override
    public void cancelOrder(Long orderId) {
        //查询订单是否存在
        TbOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        //根据订单id查询该订单对应的订单明细
        Example example = new Example(TbOrderItem.class);
        example.createCriteria().andEqualTo("orderId", orderId);
        List<TbOrderItem> orderItemList = orderItemMapper.selectByExample(example);
        //删除订单
        orderMapper.deleteByPrimaryKey(orderId);
        //删除订单对应的订单明细
        if (orderItemList!=null && orderItemList.size()>0){
            for (TbOrderItem orderItem : orderItemList) {
                orderItemMapper.deleteByPrimaryKey(orderItem.getId());
            }
        }
    }
}
