package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/brand")
@RestController
public class BrandController {

    @Reference
    private BrandService brandService;

    @GetMapping("/findAll")
    public List<TbBrand> findAll() {
        // return brandService.queryAll();
        return brandService.findAll();
    }

    @GetMapping("/testPage")
    public List<TbBrand> testPage(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "rows", defaultValue = "10") Integer rows) {
        // return brandService.testPage(page,rows);
        return (List<TbBrand>) brandService.findPage(page, rows).getRows();
    }

    @PostMapping("/findPageByCondition")
    public PageResult findPageByCondition(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "rows", defaultValue = "10") Integer rows,
            @RequestBody TbBrand tbBrand) {
        return brandService.findPageByCondition(page,rows,tbBrand);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {
            brandService.add(tbBrand);
            return Result.ok("添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("添加失败!");
    }

    @GetMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand);
            return Result.ok("添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("添加失败!");
    }

    @GetMapping("/deleteByIds")
    public Result deleteByIds(Long[] ids){
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

}
