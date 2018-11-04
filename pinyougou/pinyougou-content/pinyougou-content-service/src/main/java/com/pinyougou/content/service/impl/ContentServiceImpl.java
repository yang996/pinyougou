package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String REDIS_CONTENT="content";
    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> list=null;
        try {
            //先从redis数据库中查询
            list= (List<TbContent>) redisTemplate.boundHashOps(REDIS_CONTENT).get(categoryId);
            if(list!=null){
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //缓存数据库中没读取到,再到mysql数据库中读取
        Example example=new Example(TbContent.class);
        //根据内容类目id和启用状态查询
        example.createCriteria().
                andEqualTo("categoryId",categoryId).andEqualTo("status","1");
        //降序查询
        example.orderBy("sortOrder").desc();
        List<TbContent> contentList = contentMapper.selectByExample(example);

        try {
            //设置某个分类对应的广告内容列表到缓存数据库
            redisTemplate.boundHashOps(REDIS_CONTENT).put(categoryId,contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentList;
    }

    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);
        //新增广告内容的时候,将广告内容对应的分类从缓存数据库中删除
        updateContentByCategoryId(tbContent.getCategoryId());
    }


    @Override
    public void update(TbContent tbContent) {
        TbContent oldContent = findOne(tbContent.getId());
       //是否修改了内容分类,修改了内容分类则需要将新旧内容分类对应的内容列表都删除
        if (!tbContent.getCategoryId().equals(oldContent.getCategoryId())){
            updateContentByCategoryId(oldContent.getCategoryId());
        }
        //删除新内容分类
        updateContentByCategoryId(tbContent.getCategoryId());
        super.update(tbContent);
    }

    @Override
    public void deleteByIds(Serializable[] ids) {
        Example example=new Example(TbContent.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        List<TbContent> contentList = contentMapper.selectByExample(example);
        //将要删除的内容对应的分类数据从缓存数据库中删除
        for (TbContent tbContent : contentList) {
            updateContentByCategoryId(tbContent.getCategoryId());
        }
        //删除数据库中的数据
        super.deleteByIds(ids);
    }

    private void updateContentByCategoryId(Long categoryId) {
        try {
            redisTemplate.boundHashOps(REDIS_CONTENT).delete(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
