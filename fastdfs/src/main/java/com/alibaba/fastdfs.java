package com.alibaba;

import javafx.scene.Camera;
import org.csource.fastdfs.*;
import org.junit.Test;

/**
 * @ClassName fastdfs
 * @Author WuYeYang
 * @Description 使用fastdfs测试文件上传
 * @Date 2018/10/25 16:15
 * @Version 1.0
 **/
public class fastdfs {

    @Test
    public void fastdfsTest()throws Exception{
        String conf_filename=ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();
        //设置全局参数
        ClientGlobal.init(conf_filename);
        //追踪服务器客户端
        TrackerClient trackerClient=new TrackerClient();
        //追踪服务器
        TrackerServer trackerServer = trackerClient.getConnection();
        //存储服务器
        StorageServer storageServer=null;
        //存储服务器客户端
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        // 上传文件
        /**
         *  参数 1 ：文件
         *  参数 2 ：文件的后缀
         *  参数 3 ：文件的属性信息
         * */
        String[] upload_file =
                storageClient.upload_file("C:\\Users\\acer-pc\\Pictures\\Camera Roll\\showphoto.jpg", "jpg", null);
        if (upload_file!=null && upload_file.length>0){
            for (String s : upload_file) {
                System.out.println(s);
            }
        }
       //获取存储服务器信息
        String groupName=upload_file[0];
        String fileName=upload_file[1];

        ServerInfo[] storages =
                trackerClient.getFetchStorages(trackerServer, groupName, fileName);
        //组合可访问的url
        String url="http://"+storages[0].getIpAddr()+"/"+groupName+"/"+fileName;
        System.out.println(url);
    }
}
