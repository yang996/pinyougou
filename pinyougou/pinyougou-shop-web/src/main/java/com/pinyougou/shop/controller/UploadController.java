package com.pinyougou.shop.controller;

import com.pinyougou.common.util.FastDFSClient;
import com.pinyougou.vo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName UploadController
 * @Author WuYeYang
 * @Description
 * @Date 2018/10/25 19:14
 * @Version 1.0
 **/
@RestController
@RequestMapping("/upload")
public class UploadController {

    @PostMapping
    public Result upload(MultipartFile file){
        try {
            FastDFSClient fastDFSClient=new FastDFSClient("classpath:fastdfs/tracker.conf");
            //获取文件扩展名
            String fileExtName =
                    file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")+1);
            String url = fastDFSClient.uploadFile(file.getBytes(), fileExtName);
            if (!StringUtils.isEmpty(url)){
                return Result.ok(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("上传图片失败");
    }
}
