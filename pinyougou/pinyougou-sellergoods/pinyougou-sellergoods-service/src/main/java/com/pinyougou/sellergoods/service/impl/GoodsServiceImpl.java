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
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(interfaceClass = GoodsService.class,timeout = 200000)
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
        /*if(!StringUtils.isEmpty(goods.get***())){
            criteria.andLike("***", "%" + goods.get***() + "%");
        }*/

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
        //设置商家名称
        TbSeller seller =
                sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getName());
        //设置商家id
        item.setSellerId(goods.getGoods().getSellerId());
    }
}
