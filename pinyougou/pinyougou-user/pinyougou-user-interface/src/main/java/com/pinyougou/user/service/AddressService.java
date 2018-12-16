package com.pinyougou.user.service;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface AddressService extends BaseService<TbAddress> {

    PageResult search(Integer page, Integer rows, TbAddress address);

    /**
     * 新增收获地址
     * @param address
     */
    void addAddress(TbAddress address);
}