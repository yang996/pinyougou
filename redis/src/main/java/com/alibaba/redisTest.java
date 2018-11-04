package com.alibaba;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

/**
 * @ClassName redisTest
 * @Author WuYeYang
 * @Description
 * @Date 2018/10/29 20:34
 * @Version 1.0
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class redisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        redisTemplate.boundValueOps("wyy").set("wyy");
        Object str = redisTemplate.boundValueOps("wyy").get();
        System.out.println(str);
    }
    @Test
    public void testList(){
        redisTemplate.boundListOps("wyy2").rightPush("c");
        redisTemplate.boundListOps("wyy2").leftPush("b");
        redisTemplate.boundListOps("wyy2").rightPush("d");
        redisTemplate.boundListOps("wyy2").leftPush("a");
        List wyy = redisTemplate.boundListOps("wyy2").range(0, -1);
        System.out.println(wyy);
    }

    @Test
    public void testSet(){
        redisTemplate.boundSetOps("wyy1").add(1,1,2,3,5,"wyy");
        Set wyy = redisTemplate.boundSetOps("wyy1").members();
        System.out.println(wyy);
    }

    @Test
    public void testHash() {
        redisTemplate.boundHashOps("hash_key").put("f1", "v1");
        redisTemplate.boundHashOps("hash_key").put("f2", "v2");
        List list = redisTemplate.boundHashOps("hash_key").values();
        System.out.println(list);
    }

}
