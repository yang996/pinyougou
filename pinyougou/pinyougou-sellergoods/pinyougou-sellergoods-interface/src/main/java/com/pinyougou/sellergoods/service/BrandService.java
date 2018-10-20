package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface BrandService  extends BaseService<TbBrand> {

    public List<TbBrand> queryAll();

    /**
     * 使用分页助手,通用mapper.测试
     * @param page
     * @param rows
     * @return
     */
    List<TbBrand> testPage(Integer page, Integer rows);


    /**
     * 多条件分页查询
     * @param page
     * @param rows
     * @param tbBrand
     * @return
     */
    PageResult findPageByCondition(Integer page, Integer rows,TbBrand tbBrand);
}
