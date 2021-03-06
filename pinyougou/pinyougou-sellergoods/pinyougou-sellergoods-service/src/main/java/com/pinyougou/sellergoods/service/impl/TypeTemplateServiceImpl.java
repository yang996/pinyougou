package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service(interfaceClass = TypeTemplateService.class)
public class TypeTemplateServiceImpl extends BaseServiceImpl<TbTypeTemplate> implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult findAll(Integer page,Integer rows,TbTypeTemplate tbTypeTemplate) {
        //分页查询
        PageHelper.startPage(page,rows);
        Example example=new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(tbTypeTemplate.getName())){
            criteria.andLike("name","%"+tbTypeTemplate.getName()+"%");
        }
        List<TbTypeTemplate> typeTemplateList =
                typeTemplateMapper.selectByExample(example);
        //分页信息
        PageInfo<TbTypeTemplate> pageInfo = new PageInfo<>(typeTemplateList);
        //返回结果
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public void delete(Long[] ids) {
        Example example=new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        typeTemplateMapper.deleteByExample(example);
    }

    @Override
    public List<Map> findSpecList(Long id) {
        if (StringUtils.isEmpty(id)){
            return null;
        }
        TbTypeTemplate typeTemplate = findOne(id);
        //查询规格
        List<Map> specList = JSONArray.parseArray(typeTemplate.getSpecIds(), Map.class);
        //规格对应的选项
        for (Map map : specList) {
            TbSpecificationOption parm=new TbSpecificationOption();
            parm.setSpecId(Long.parseLong(map.get("id").toString()));
            List<TbSpecificationOption> options = specificationOptionMapper.select(parm);
            map.put("options",options);
        }
        return specList;
    }
}
