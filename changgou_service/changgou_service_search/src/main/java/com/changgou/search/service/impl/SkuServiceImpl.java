package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuEsMapper skuEsMapper;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Override
    public void importSku() {
        Result<List<Sku>> skuListResult = skuFeign.findByStatus("1");
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skuListResult.getData()), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos) {
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec());
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfos);
    }

    @Override
    public Map search(Map<String, String> searchMap) {
//        System.out.println(searchMap);
        Map map = new HashMap();
        NativeSearchQueryBuilder builder = builderBasicQuery(searchMap);
        searchList(map, builder);
        searchCategoryList(map, builder);
        searchBrandList(map, builder);
        searchSpec(map, builder);
//        System.out.println(map);
        return map;
    }

    public NativeSearchQueryBuilder builderBasicQuery(Map<String, String> searchMap) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        if (searchMap != null) {
            //bool查询
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //关键字查询
            String keywords = searchMap.get("keywords") == null ? "" : searchMap.get("keywords");
            if (StringUtils.isNotEmpty(keywords)) {
//                builder.withQuery(QueryBuilders.matchQuery("name", keywords));
                boolQueryBuilder.must(QueryBuilders.matchQuery("name", keywords));
            }
            //分类搜索
            String category = searchMap.get("category") == null ? "" : searchMap.get("category");
            if (StringUtils.isNotEmpty(category)) {
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", category));
            }
            //品牌搜索
            String brand = searchMap.get("brand") == null ? "" : searchMap.get("brand");
            if (StringUtils.isNotEmpty(brand)) {
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName", brand));
            }
            //规格查询
            for (String key : searchMap.keySet()) {
                if (key.startsWith("spec_")) {
                    String specField = "specMap" + key.substring(5) + ".keyword";
                    boolQueryBuilder.must(QueryBuilders.termQuery(specField, searchMap.get(key)));
                }
            }
            builder.withQuery(boolQueryBuilder);
        }
        return builder;
    }

    public void searchList(Map map, NativeSearchQueryBuilder builder) {
        NativeSearchQuery query = builder.build();
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(query, SkuInfo.class);
        map.put("rows", page.getContent());
        map.put("total", page.getTotalElements());
        map.put("totalPages", page.getTotalPages());
    }

    public void searchCategoryList(Map map, NativeSearchQueryBuilder builder) {
        //1.设置分组域名
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_category").field("categoryName");
        //2.添加分组查询参数
        builder.addAggregation(termsAggregationBuilder);
        //3.执行搜索
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集
        Aggregations aggregations = page.getAggregations();
        //5.提取分组结果数据
        StringTerms stringTerms = aggregations.get("group_category");
        //6.定义分类名字列表
        List<String> categoryList = new ArrayList<>();
        //7.遍历读取分组查询结果
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1.遍历读取分组名字
            categoryList.add(bucket.getKeyAsString());
        }
        //8.返回分类数据列表
        map.put("categoryList", categoryList);
    }

    public void searchBrandList(Map map, NativeSearchQueryBuilder builder) {
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_brand").field("brandName");
        builder.addAggregation(termsAggregationBuilder);
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        Aggregations aggregations = page.getAggregations();
        StringTerms stringTerms = aggregations.get("group_brand");
        List<String> brandList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            brandList.add(bucket.getKeyAsString());
        }
        map.put("brandList", brandList);
    }

    public void searchSpec(Map map, NativeSearchQueryBuilder builder) {
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_spec").field("spec.keyword").size(10000);
        builder.addAggregation(termsAggregationBuilder);
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        Aggregations aggregations = page.getAggregations();
        StringTerms stringTerms = aggregations.get("group_spec");
        List<String> specList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            specList.add(bucket.getKeyAsString());
        }
        Map<String, Set<String>> specMap = new HashMap<>();
        for (String spec : specList) {
            Map<String, String> tempMap = JSON.parseObject(spec, Map.class);
            for (String key : tempMap.keySet()) {
                Set<String> values = specMap.get(key);
                if (values == null) {
                    values = new HashSet<String>();
                }
                values.add(tempMap.get(key));
                specMap.put(key, values);
            }
        }
        map.put("specList", specMap);
    }

    //分组
    public void searchGroup(Map map, NativeSearchQueryBuilder builder) {
        builder.addAggregation(AggregationBuilders.terms("group_category").field("categoryName"));
        builder.addAggregation(AggregationBuilders.terms("group_brand").field("brandName"));
        builder.addAggregation(AggregationBuilders.terms("group_spec").field("spec.keyword").size(10000));
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        Aggregations aggregations = page.getAggregations();
        List<String> groupCategory = getGroupResult(aggregations, "group_category");
        map.put("categoryList", groupCategory);
        List<String> groupBrand = getGroupResult(aggregations, "group_brand");
        map.put("brandList", groupBrand);
        List<String> groupSpec = getGroupResult(aggregations, "group_spec");
        Map<String, Set<String>> specMap = new HashMap<>();
        for (String spec : groupSpec) {
            Map<String, String> tempMap = JSON.parseObject(spec, Map.class);
            for (String key : tempMap.keySet()) {
                Set<String> values = specMap.get(key);
                if (values == null) {
                    values = new HashSet<String>();
                }
                values.add(tempMap.get(key));
                specMap.put(key, values);
            }
        }
        map.put("specMap", specMap);
    }

    //获取结果集
    public List<String> getGroupResult(Aggregations aggregations, String group_ame) {
        StringTerms stringTerms = aggregations.get(group_ame);
        List<String> specList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            specList.add(bucket.getKeyAsString());
        }
        return specList;
    }
}
