package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContent;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface ContentService extends BaseService<TbContent> {

    /**
     * 查询内容
     * @param page 当前页
     * @param rows 页大小
     * @param content 查询条件
     * @return
     */
    PageResult search(Integer page, Integer rows, TbContent content);


    /**
     * 根据内容分类id查询内容列表
     * @param categoryId 内容分类id
     * @return
     */
    List<TbContent> findContentListByCategoryId(Long categoryId);
}