package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = SpecificationService.class)
public class SpecificationServiceImpl extends BaseServiceImpl<TbSpecification> implements SpecificationService {

    @Autowired
    SpecificationMapper specificationMapper;

    @Autowired
    SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult findAll(Integer page, Integer rows, TbSpecification specification) {

        //分页
        PageHelper.startPage(page, rows);
        //查询条件(where)
        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(specification.getSpecName())) {
            criteria.andLike("specName", "%" + specification.getSpecName() + "%");
        }
        List<TbSpecification> specificationList =
                specificationMapper.selectByExample(example);
        //分页信息
        PageInfo<TbSpecification> pageInfo = new PageInfo<>(specificationList);
        //获取结果返回前端
        PageResult pageResult = new PageResult(pageInfo.getTotal(), pageInfo.getList());
        return pageResult;
    }

    @Override
    public void add(Specification specification) {
        //添加规格
        add(specification.getSpecification());
        List<TbSpecificationOption> specificationOptionList =
                specification.getSpecificationOptionList();
        //添加规格选项
        if (specificationOptionList!=null && specificationOptionList.size()>0){
            for (TbSpecificationOption specificationOption : specificationOptionList) {
                specificationOption.setSpecId(specification.getSpecification().getId());
                specificationOptionMapper.insertSelective(specificationOption);
            }
        }
    }

    @Override
    public Specification findOne(Long id) {
        //查询规格
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

        TbSpecificationOption parm=new TbSpecificationOption();
        parm.setSpecId(id);
        //查询规格选项信息
        List<TbSpecificationOption> tbSpecificationOptionList =
                specificationOptionMapper.select(parm);
        //查询结果返回
        Specification specification=new Specification(tbSpecification,tbSpecificationOptionList);
        return specification;
    }

    @Override
    public void update(Specification specification) {
        //更新规格信息
        TbSpecification tbSpecification = specification.getSpecification();
        update(tbSpecification);

        //先删除规格选项信息
        TbSpecificationOption parm=new TbSpecificationOption();
        parm.setSpecId(specification.getSpecification().getId());
        specificationOptionMapper.delete(parm);

        //更新规格选项信息
        List<TbSpecificationOption> specificationOptionList =
                specification.getSpecificationOptionList();
        if (specificationOptionList!=null && specificationOptionList.size()>0){
            for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
                tbSpecificationOption.setSpecId(specification.getSpecification().getId());
                specificationOptionMapper.insertSelective(tbSpecificationOption);
            }
        }

    }

    @Override
    public void deleteSpecificationByIds(Long[] ids) {
        //批量删除规格
       deleteByIds(ids);
       //批量删除规格选项
        Example example=new Example(TbSpecificationOption.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("specId", Arrays.asList(ids));
        specificationOptionMapper.deleteByExample(example);
    }
}
