package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    void addGoods(Goods goods);

    /**
     * 查询商品信息
     * @param id 商品id
     * @return 商品对象vo
     */
    Goods findGoods(Long id);

    /**
     * 更新商品信息
     * @param goods
     */
    void updateGoods(Goods goods);

    /**
     * 审核商品
     * @param ids 选中的商品id
     * @param status 商品状态
     */
    void updateStatus(Long[] ids, String status);

    /**
     * 删除商品,只是更改商品状态
     * @param ids 商品id
     */
    void deleteGoodsByIds(Long[] ids);


    /**
     * 商品上架
     * @param ids 选中的商品
     * @param status 商品上架的状态
     */
    void isMarketable(Long[] ids, String status);

    /**
     * 根据商品goodsId和商品sku的status查询商品sku列表
     * @param ids
     * @param status
     * @return
     */
    List<TbItem> findItemByGoodsIdAndStatus(Long[] ids, String status);

    /**
     * 根据商品id和商品状态查询商品
     * @param goodsId
     * @param s
     * @return
     */
    Goods findGoodsByIdAndStatus(Long goodsId, String s);
}
