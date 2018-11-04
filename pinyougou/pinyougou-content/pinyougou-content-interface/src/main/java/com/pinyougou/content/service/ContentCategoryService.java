package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContentCategory;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface ContentCategoryService extends BaseService<TbContentCategory> {

    /**
     * 查询内容分类
     * @param page 当前页
     * @param rows 页大小
     * @param contentCategory 查询条件
     * @return
     */
    PageResult search(Integer page, Integer rows, TbContentCategory contentCategory);
}