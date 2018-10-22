package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<TbBrand>{

    //继承通用mapper,原来的方式不受影响
    public List<TbBrand> queryAll();

    //查询品牌列表
    List<Map<String,String>> selectOptionList();

}
