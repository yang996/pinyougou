package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface SellerService extends BaseService<TbSeller> {

    /**
     * 查询商家信息
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    PageResult search(Integer page, Integer rows, TbSeller seller);

    /**
     * 更新商家状态
     * @param tbSeller
     */
    void updateStatus(TbSeller tbSeller);

    /**
     * 商家修改密码
     * @param sellerId 商家id
     * @param password 密码
     */
    void updatePassword(String sellerId,String password);
}