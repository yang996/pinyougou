package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ItemController {

    @Reference
    private ItemCatService itemCatService;

    @Reference
    private GoodsService goodsService;


    /**
     * 接收商品spu id，根据该Id查询3级分类中文名称、基本、描述、sku列表
     * @param goodsId 商品spu id
     * @return 视图名称和数据
     */
    @GetMapping("/{goodsId}")
    public ModelAndView toItemPage(@PathVariable("goodsId")Long goodsId){
        ModelAndView mv = new ModelAndView("item");

        //根据商品spu id查询商品信息（基本、描述、sku列表(已启用；并且按照是否默认排序降序排序)）
        //返回的sku列表要按照是否默认降序排序是因为在详情页面刚进入的时候应该要默认显示一个sku
        Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");

        //根据商品id查询商品基本信息获取3级商品分类id;再根据分类id查询分类
        //itemCat1 1级商品分类中文名称
        TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
        mv.addObject("itemCat1", itemCat1.getName());

        //itemCat2 2级商品分类中文名称
        TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
        mv.addObject("itemCat2", itemCat2.getName());

        //itemCat3 3级商品分类中文名称
        TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
        mv.addObject("itemCat3", itemCat3.getName());

        //goodsDesc 商品描述信息
        mv.addObject("goodsDesc", goods.getGoodsDesc());
        //goods 商品基本信息
        mv.addObject("goods", goods.getGoods());
        //itemList 商品sku列表
        mv.addObject("itemList", goods.getItemList());

        return mv;
    }
}
