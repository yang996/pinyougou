package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 新增商品
     * @param goods
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //设置商家名称
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(name);
            goods.getGoods().setAuditStatus("0");//未审核
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    /**
     * 根据id查询商品信息
     * @param id
     * @return
     */
    @GetMapping("/findOne")
    public Goods findGoods(Long id) {
        return goodsService.findGoods(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            //判断当前商家是否是同一个商家
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!sellerId.equals(goods.getGoods().getSellerId()))
            {
                return Result.fail("操作非法");
            }
            goodsService.updateGoods(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteGoodsByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param goods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                              @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        //设置商家id;
        goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
        return goodsService.search(page, rows, goods);
    }


    /**
     * 将商品提交审核
     * @return 审核结果
     */
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            goodsService.updateStatus(ids,status);
            return Result.ok("操作成功成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("操作失败");
        }
    }

    /**
     * 商品上架或者下架
     * @return 操作结果
     */
    @GetMapping("/isMarketable")
    public Result isMarketable(Long[] ids,String status){
        try {
           goodsService.isMarketable(ids, status);
            return Result.ok("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.ok("操作失败");
        }
    }

}
