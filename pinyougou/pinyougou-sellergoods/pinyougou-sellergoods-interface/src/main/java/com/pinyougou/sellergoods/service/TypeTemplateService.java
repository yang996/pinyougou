package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据模板id查询规格及规格对应的选项
     * @param id 模板id
     * @return 规格及其选项
     * */
    List<Map> findSpecList(Long id);
}


