package com.alibaba;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName Freemarker
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/2 19:05
 * @Version 1.0
 **/
public class Freemarker {

    @Test
    public void test() throws Exception{
        //创建配置对象,指定版本号
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置默认生成文件编码
        configuration.setDefaultEncoding("utf-8");
        //设置模板路径
        configuration.setClassForTemplateLoading(Freemarker.class,"/ftl");
        //获取模板
        Template template = configuration.getTemplate("test.ftl");
        //加载数据
        Map<String, Object> dataModel = new HashMap<String, Object>();
        ArrayList<Map<String,Object>> arrayList = new ArrayList<Map<String,Object>>();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name","阿里巴巴");
        data.put("message","马云");
        Map<String, Object> data1 = new HashMap<String, Object>();
        data1.put("name","腾讯");
        data1.put("message","马化腾");
        arrayList.add(data);
        arrayList.add(data1);
        dataModel.put("arrayList",arrayList);
        dataModel.put("date",new Date());
        dataModel.put("number",123123123);
        //创建输出对象
        FileWriter fileWriter = new FileWriter("E:\\alibaba\\test\\test.html");
        //渲染模板和数据
        template.process(dataModel,fileWriter);
        //关闭输出
        fileWriter.close();
    }

}
