package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService extends BaseService<TbSpecification> {

    /**
     * @param page 当前页
     * @param rows 页大小
     * @param specification 请求参数
     * @return PageResult,结果对象
     */
    PageResult findAll(Integer page, Integer rows, TbSpecification specification);

    /**
     * 添加规格及其选项信息
     * @param specification
     */
    void add(Specification specification);

    /**
     * 根据主键查询规格信息
     * @param id
     * @return
     */
    Specification findOne(Long id);


    /**
     * 更新规格信息
     * @param specification
     */
    void update(Specification specification);

    /**
     * 删除根据id规格信息
     * @param ids
     */
    void deleteSpecificationByIds(Long[] ids);

    List<Map<String,String>> selectOptionList();
}
