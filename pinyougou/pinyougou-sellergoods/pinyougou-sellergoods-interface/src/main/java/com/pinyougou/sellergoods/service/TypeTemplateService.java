package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    /**
     * 根据条件查询分页所有信息
     * @param page
     * @param rows
     * @param tbTypeTemplate
     * @return
     */
    PageResult findAll(Integer page,Integer rows,TbTypeTemplate tbTypeTemplate);

    /**
     * 批量删除信息
     * @param ids
     */
    void delete(Long[] ids);

}


