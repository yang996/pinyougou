package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import com.pinyougou.vo.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/specification")
@RestController
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    /**
     *
     * @param page 当前页
     * @param rows 页大小
     * @param specification 请求参数
     * @return PageResult,结果对象
     */
    @PostMapping("/search")
    public PageResult search(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "10") Integer rows,
            @RequestBody TbSpecification specification){
        return specificationService.findAll(page,rows,specification);
    }

    /**
     * 添加规格及其选项
     * @param specification
     * @return Result添加成功或失败信息
     */
    @PostMapping("/add")
    public Result add(
            @RequestBody Specification specification){
        try {
            specificationService.add(specification);
            return Result.ok("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("添加失败");
    }

    /**
     * 删除信息
     * @param ids
     * @return Result删除结果
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            specificationService.deleteSpecificationByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 根据主键查询规格信息
     * @param id
     * @return
     */
    @GetMapping("/findOne")
    public Specification findOne(Long id){
       return specificationService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Specification specification){
        try {
            specificationService.update(specification);
            return Result.ok("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新失败");
    }

    /**
     * 查询规格数据
     * @return
     */
    @GetMapping("/selectOptionList")
    public List<Map<String,String>> selectOptionList(){
        return specificationService.selectOptionList();
    }
}
