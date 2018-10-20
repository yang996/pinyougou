package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<TbBrand>{

    //继承通用mapper,原来的方式不受影响
    public List<TbBrand> queryAll();


}
