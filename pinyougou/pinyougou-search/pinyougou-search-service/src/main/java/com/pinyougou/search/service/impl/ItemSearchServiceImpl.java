package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @ClassName ItemSearchServiceImpl
 * @Author WuYeYang
 * @Description
 * @Date 2018/10/31 10:17
 * @Version 1.0
 **/
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    public Map<String, Object> itemSerach(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();

        //处理关键字中的空格问题
        if (!StringUtils.isEmpty(searchMap.get("keywords"))) {
            searchMap.put("keywords", searchMap.get("keywords").toString().replaceAll(" ", ""));
        }
        //创建查询条件对象
        //SimpleQuery query = new SimpleQuery();
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        //设置查询关键字
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //按照分类过滤
        if (!StringUtils.isEmpty(searchMap.get("category"))) {
            Criteria categoryCriteria =
                    new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(filterQuery);
        }

        //按照品牌过滤
        if (!StringUtils.isEmpty(searchMap.get("brand"))) {
            Criteria brandCriteria =
                    new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(filterQuery);
        }

        //按照规格过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> map = (Map<String, String>) searchMap.get("spec");
            Set<Map.Entry<String, String>> entrySet = map.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                Criteria specCriteria =
                        new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //按照价格过滤
        if (!StringUtils.isEmpty(searchMap.get("price"))) {
            String[] prices = searchMap.get("price").toString().split("-");
            //大于等于起始价格
            SimpleFilterQuery start =
                    new SimpleFilterQuery(new Criteria("item_price").greaterThanEqual(prices[0]));
            query.addFilterQuery(start);
            //小于等于结束价格
            if (!"*".equals(prices[1])) {
                SimpleFilterQuery end =
                        new SimpleFilterQuery(new Criteria("item_price").lessThanEqual(prices[1]));
                query.addFilterQuery(end);
            }
        }

        //当前页
        int pageNo = 1;
        //页大小
        int pageSize = 20;
        if (searchMap.get("pageNo") != null) {
            pageNo = Integer.parseInt(searchMap.get("pageNo").toString());
        }
        if (searchMap.get("pageSize") != null) {
            pageSize = Integer.parseInt(searchMap.get("pageSize").toString());
        }
        query.setOffset((pageNo - 1) * pageSize);
        query.setRows(pageSize);

        //设置排序
        if (!StringUtils.isEmpty(searchMap.get("sortField")) && !StringUtils.isEmpty(searchMap.get("sort"))) {
            String sortOrder = searchMap.get("sort").toString();
            Sort sort = new Sort(sortOrder.equals("DESC") ?
                    Sort.Direction.DESC : Sort.Direction.ASC, "item_" + searchMap.get("sortField").toString());
            query.addSort(sort);
        }

        //设置高亮
        HighlightOptions highlightOptions = new HighlightOptions();

        //添加一个要高亮显示的域名
        highlightOptions.addField("item_title");
        //高亮起始标签
        highlightOptions.setSimplePrefix("<font style='color:red'>");
        //高亮结束标签
        highlightOptions.setSimplePostfix("</font>");
        query.setHighlightOptions(highlightOptions);

        //分页查询
        //ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //处理高亮标题
        List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();
        if (highlighted != null && highlighted.size() > 0) {
            for (HighlightEntry<TbItem> entry : highlighted) {
                if (entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets() != null) {
                    //设置的是返回回来的那些商品标题
                    entry.getEntity().setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
                }
            }
        }

        //当前页数据列表
        resultMap.put("rows", highlightPage.getContent());
        //总页数
        resultMap.put("totalPages", highlightPage.getTotalPages());
        //总记录数
        resultMap.put("total", highlightPage.getTotalElements());
        return resultMap;
    }

    @Override
    public void importItemList(List<TbItem> itemList) {
        //将商品sku的spec中的json字符串转换为map对象
        for (TbItem item : itemList) {
            Map specMap = JSONObject.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void deleteItemByGoodsId(Long[] ids) {
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        SimpleQuery query=new SimpleQuery(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
