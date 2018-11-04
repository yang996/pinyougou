package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Transactional
@Service(interfaceClass = GoodsService.class, timeout = 200000)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(goods.getAuditStatus())) {
            criteria.andEqualTo("auditStatus", goods.getAuditStatus());
        }
        if (!StringUtils.isEmpty(goods.getGoodsName())) {
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }
        if (!StringUtils.isEmpty(goods.getSellerId())) {
            criteria.andEqualTo("sellerId", goods.getSellerId());
        }
        //不查询删除的商品
        criteria.andNotEqualTo("isDelete","1");

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void addGoods(Goods goods) {
        //添加商品基本信息
        goodsMapper.insertSelective(goods.getGoods());
        //添加商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
        //添加商品sku列表
        addItem(goods);
    }

    @Override
    public Goods findGoods(Long id) {
        Goods goods = new Goods();
        //商品基本信息
        goods.setGoods(goodsMapper.selectByPrimaryKey(id));
        //商品描述信息
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
        //商品sku列表
        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsId", id);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
    }

    @Override
    public void updateGoods(Goods goods) {
        //更新商品基本信息,重新设置为未审核
        goods.getGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());
        //更行商品描述信息
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
        //先删除商品sku信息
        TbItem parm = new TbItem();
        parm.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(parm);
        //保存商品信息
        addItem(goods);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        //设置商品状态
        TbGoods goods = new TbGoods();
        goods.setAuditStatus(status);
        //更新条件
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(goods, example);
        //商品状态为2,表示商品审核通过.然后将商品sku状态改为上架 1
        if ("2".equals(status)) {
            TbItem item = new TbItem();
            item.setStatus("1");
            Example example1 = new Example(TbItem.class);
            example1.createCriteria().andIn("goodsId", Arrays.asList(ids));
            itemMapper.updateByExampleSelective(item, example1);
        }
    }

    private void addItem(Goods goods) {
        System.out.println(goods.getGoods().getIsEnableSpec());
        //启用规格
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            //添加商品sku信息
            if (!StringUtils.isEmpty(goods.getItemList())) {
                List<TbItem> itemList = goods.getItemList();
                for (TbItem item : itemList) {
                    //设置标题
                    String title = goods.getGoods().getGoodsName();
                    Map<String, Object> map = JSON.parseObject(item.getSpec());
                    Set<Map.Entry<String, Object>> entrySet = map.entrySet();
                    for (Map.Entry<String, Object> entry : entrySet) {
                        title += " " + entry.getValue();
                    }
                    item.setTitle(title);
                    setItemValue(goods, item);
                    itemMapper.insertSelective(item);
                }
            }
        } else {
            //不启用规格
            TbItem item = new TbItem();
            //商品标题,就是spu标题
            item.setTitle(goods.getGoods().getGoodsName());
            //商品价格
            item.setPrice(goods.getGoods().getPrice());
            //商品数量
            item.setNum(9999);
            item.setStatus("0");
            item.setIsDefault("1");
            item.setSpec("{}");
            setItemValue(goods, item);
            itemMapper.insertSelective(item);
        }
    }


    private void setItemValue(Goods goods, TbItem item) {
        //设置品牌
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //设置类别
        TbItemCat itemCat =
                itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        item.setCategoryid(itemCat.getId());
        //设置商品spu的id
        item.setGoodsId(goods.getGoods().getId());
        //设置商品图片
        List<Map> imgList =
                JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imgList != null && imgList.size() > 0) {
            item.setImage(imgList.get(0).get("url").toString());
        }
        //设置创建时间和更新时间
        item.setCreateTime(new Date());
        item.setUpdateTime(item.getCreateTime());
        //设置商品价格
        item.setPrice(goods.getGoods().getPrice());
        //设置商家名称
        TbSeller seller =
                sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getName());
        //设置商家id
        item.setSellerId(goods.getGoods().getSellerId());
    }

    @Override
    public void deleteGoodsByIds(Long[] ids) {
        TbGoods goods=new TbGoods();
        goods.setIsDelete("1");
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(goods,example);
    }

    @Override
    public void isMarketable(Long[] ids, String status) {
        Example example=new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        //获取要上架的商品
        List<TbGoods> goodsList = goodsMapper.selectByExample(example);
        for (TbGoods goods : goodsList) {
            //商品审核通过才能上架
            if ("2".equals(goods.getAuditStatus())){
                //商品上架
                goods.setIsMarketable(status);
                goodsMapper.updateByPrimaryKeySelective(goods);
            }
        }
    }

    @Override
    public List<TbItem> findItemByGoodsIdAndStatus(Long[] ids, String status) {
        Example example=new Example(TbItem.class);
        example.createCriteria().
                andIn("goodsId",Arrays.asList(ids)).
                andEqualTo("status",status);
        return itemMapper.selectByExample(example);
    }

    @Override
    public Goods findGoodsByIdAndStatus(Long goodsId, String s) {
        Goods goods = new Goods();
        //商品基本信息
        goods.setGoods(goodsMapper.selectByPrimaryKey(goodsId));
        //商品描述信息
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(goodsId));
        //商品sku列表
        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsId", goodsId);
        if (!StringUtils.isEmpty(s)){
            criteria.andEqualTo("status",s);
        }
        //按照是否默认值降序排序,默认值为,否则为0;
        example.orderBy("isDefault").desc();
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
    }
}
