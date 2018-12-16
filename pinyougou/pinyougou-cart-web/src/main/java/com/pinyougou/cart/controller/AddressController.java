package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/address")
@RestController
public class AddressController {

    @Reference
    private AddressService addressService;

    /**
     * 新增收获地址
     * @return
     */
    @PostMapping("/addAddress")
    public Result addAddress(@RequestBody TbAddress address){
        try {
            String username =
                    SecurityContextHolder.getContext().getAuthentication().getName();
            address.setUserId(username);
            addressService.addAddress(address);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    /**
     * 根据用户名获取用户收获地址并返回
     * @return
     */
   @GetMapping("/findAddressList")
   public List<TbAddress> findAddressList(){
       TbAddress address=new TbAddress();
       String username= SecurityContextHolder.getContext().getAuthentication().getName();
       address.setUserId(username);
       List<TbAddress> addressList = addressService.findByWhere(address);
       return addressList;
   }
    @PostMapping("/add")
    public Result add(@RequestBody TbAddress address) {
        try {
            addressService.add(address);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbAddress findOne(Long id) {
        return addressService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbAddress address) {
        try {
            addressService.update(address);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            addressService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param address 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbAddress address, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return addressService.search(page, rows, address);
    }

}
