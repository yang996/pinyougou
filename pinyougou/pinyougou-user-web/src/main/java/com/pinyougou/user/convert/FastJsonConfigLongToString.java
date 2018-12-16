package com.pinyougou.user.convert;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;

import java.math.BigInteger;

/**
 * @ClassName FastJsonConfigLongToString
 * @Author WuYeYang
 * @Description 将long类型转换为string
 * @Date 2018/11/17 8:06
 * @Version 1.0
 **/
public class FastJsonConfigLongToString extends FastJsonConfig {

    public FastJsonConfigLongToString() {
        super();
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        this.setSerializeConfig(serializeConfig);
    }
}

