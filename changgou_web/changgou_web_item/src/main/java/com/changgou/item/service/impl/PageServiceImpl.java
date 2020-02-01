package com.changgou.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.item.service.PageService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PageServiceImpl implements PageService {
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private SpuFeign spuFeign;
    @Autowired
    private CategoryFeign categoryFeign;
    @Value("${pagepath}")
    private String pagePath;

    @Override
    public void createPageHtml(Long spuId) {
        try {
            //1.创建上下文对象
            Context context = new Context();
            Map<String, Object> map = buildDataModel(spuId);
            context.setVariables(map);
            //2.识别并生成静态页目录
            File dir = new File(pagePath);
            if (!dir.exists()) {
                //创建联级目录
                dir.mkdirs();
            }
            //3.创建静态页面文件对象
            File dest = new File(dir, spuId + ".html");
            //4.创建文件输出对象
            PrintWriter out = new PrintWriter(dest, "UTF-8");
            //5.输出文件
            templateEngine.process("item", context, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePageHtml(Long spuId) {
        try {
            File dir = new File(pagePath);
            File file = new File(dir,spuId+".html");
            if (file.exists()){
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> buildDataModel(Long spuId) {
        //构建数据模型
        Map<String, Object> dataMap = new HashMap<>();
        //获取spu和SKU列表
        Result<Goods> result = spuFeign.findById(spuId);
        Spu spu = result.getData().getSpu();
        //获取分类信息
        dataMap.put("category1", categoryFeign.findById(spu.getCategory1Id()).getData());
        dataMap.put("category2", categoryFeign.findById(spu.getCategory2Id()).getData());
        dataMap.put("category3", categoryFeign.findById(spu.getCategory3Id()).getData());
        if (spu.getImages()!=null) {
            dataMap.put("imageList",spu.getImages().split(","));
        }
        //获取规格数据
        dataMap.put("specificationList", JSON.parseObject(spu.getSpecItems(),Map.class));
        dataMap.put("spu",spu);
        //返回sku列表
        dataMap.put("skuList",result.getData().getSkuList());
        return dataMap;
    }
}
