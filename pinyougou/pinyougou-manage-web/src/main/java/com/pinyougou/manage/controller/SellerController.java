package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/seller")
@RestController
public class SellerController {

    @Reference
    private SellerService sellerService;


    @PostMapping("/update")
    public Result update(@RequestBody TbSeller tbSeller){
        try {
            sellerService.update(tbSeller);
            return Result.ok("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("操作失败");
    }

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    @GetMapping("/findOne")
    public TbSeller findOne(String id){
        return sellerService.findOne(id);
    }


    /**
     * 分页查询列表
     * @param seller 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSeller seller,
                             @RequestParam(value = "page", defaultValue = "1")Integer page,
                             @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return sellerService.search(page, rows, seller);
    }

}
