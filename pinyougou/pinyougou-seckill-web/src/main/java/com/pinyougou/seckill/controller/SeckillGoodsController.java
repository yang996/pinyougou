package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/seckillGoods")
@RestController
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询全部秒杀商品
     * @return
     */
    @GetMapping("/findList")
    public List<TbSeckillGoods> findList(){
        return seckillGoodsService.findList();
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.add(seckillGoods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbSeckillGoods findOne(Long id) {
        return seckillGoodsService.findOneFromRedis(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.update(seckillGoods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            seckillGoodsService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param seckillGoods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSeckillGoods seckillGoods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return seckillGoodsService.search(page, rows, seckillGoods);
    }
}
