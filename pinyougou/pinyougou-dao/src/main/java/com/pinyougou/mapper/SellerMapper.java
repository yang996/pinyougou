package com.pinyougou.mapper;

import com.pinyougou.pojo.TbSeller;

import tk.mybatis.mapper.common.Mapper;

public interface SellerMapper extends Mapper<TbSeller> {
    /**
     *  根据商家id更新商家状态
     */
    void update(TbSeller tbSeller);
}
