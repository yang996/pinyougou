package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/itemCat")
@RestController
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    /**
     * 根据父id查询分类商品
     * @param parentId
     * @return
     */
    @GetMapping("/findByParentId")
    public List<TbItemCat> findByParentId(Long parentId){
        TbItemCat tbItemCat=new TbItemCat();
        tbItemCat.setParentId(parentId);
        return itemCatService.findByWhere(tbItemCat);
    }

    /**
     * 根据id查询分类商品
     * @param id
     * @return
     */
    @GetMapping("/findOne")
    public TbItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }


    /**
     * 查询所有分类
     * @return
     */
    @GetMapping("/findAll")
    public List<TbItemCat> findAll(){
        return itemCatService.findAll();
    }

}
