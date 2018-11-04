package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ItemSearchService
 * @Author WuYeYang
 * @Description
 * @Date 2018/10/31 10:16
 * @Version 1.0
 **/
public interface ItemSearchService {
    /**
     * 根据搜索条件查询商品sku列表
     * @param searchMap
     * @return
     */
   Map<String,Object> itemSerach(Map<String,Object> searchMap);

    /**
     * 将审核通过的商品sku列表添加进索引库
     * @param itemList
     */
    void importItemList(List<TbItem> itemList);

    /**
     * 运营商后台删除商品的时候,要将商品对应solr中的数据删除
     * @param ids
     */
    void deleteItemByGoodsId(Long[] ids);
}
