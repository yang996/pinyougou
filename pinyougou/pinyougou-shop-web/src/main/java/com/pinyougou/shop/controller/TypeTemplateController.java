package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/typeTemplate")
@RestController
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    /**
     * 根据主键查询分类模板信息
     * @param id
     * @return
     */
    @GetMapping("/findOne")
    public TbTypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }


    /**
     * 根据模板id查询规格及规格对应的选项
     * @param id 模板id
     * @return 规格及其选项
     * */
    @GetMapping("/findSpecList")
    public List<Map> findSpecList(Long id){

        return typeTemplateService.findSpecList(id);
    }
}
