package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/seller")
@RestController
public class SellerController {

    @Reference
    private SellerService sellerService;

    /**
     * 商家修改密码
     * @return
     */
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody Map<String,Object> map){
        try {
            //获取商家id
            String sellerId =
                    SecurityContextHolder.getContext().getAuthentication().getName();
            //根据商家id查找商家是否存在
            TbSeller seller = sellerService.findOne(sellerId);

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //原密码不正确
            if (! passwordEncoder.matches(map.get("oldPassword").toString(),seller.getPassword())){
                return Result.fail("原密码不正确");
            }else {
                if (StringUtils.isEmpty(map.get("password"))){
                    return Result.fail("新密码不能为空");
                }else {
                    //对新密码加密后保存
                    sellerService.updatePassword(sellerId,passwordEncoder.encode(map.get("password").toString()));
                    return Result.ok("修改密码成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改密码失败");
    }

    @RequestMapping("/findAll")
    public List<TbSeller> findAll() {
        return sellerService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return sellerService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbSeller seller) {
        try {
            //设置商家状态码 0：未审核   1：已审核   2：审核未通过   3：关闭
            seller.setStatus("0");
            //对商家密码加密
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            seller.setPassword(passwordEncoder.encode(seller.getPassword()));
            sellerService.add(seller);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbSeller findOne(String id) {
        return sellerService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbSeller seller) {
        try {
            sellerService.update(seller);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(String[] ids) {
        try {
            sellerService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
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
