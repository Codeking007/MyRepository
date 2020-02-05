package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SkuMapper skuMapper;

    @Override
    public void saveGoods(Goods goods) {
        //增加Spu
        Spu spu = goods.getSpu();
        //判断是否有SpuID
        if (spu.getId() != null) {
            Example example = new Example(Sku.class);
            example.createCriteria().andEqualTo("spuId", spu.getId());
            spuMapper.updateByPrimaryKeySelective(spu);
            skuMapper.deleteByExample(example);
        } else {
            spu.setId(idWorker.nextId());
            spuMapper.insertSelective(spu);
        }
        //增加Sku
        Date date = new Date();
        //查询分类
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        //查询品牌
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        //获取Sku集合
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            if (StringUtils.isEmpty(sku.getSpec())) {
                sku.setSpec("{}");
            }
            String spuName = spu.getName();
            Map<String, String> specMap = JSON.parseObject(sku.getSpec(), Map.class);
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                spuName += " " + entry.getValue();
            }
            sku.setName(spuName);

            //分布式id
            sku.setId(idWorker.nextId());
            //SpuId
            sku.setSpuId(spu.getId());
            //创建日期
            sku.setCreateTime(date);
            //修改日期
            sku.setUpdateTime(date);
            //商品分类ID
            sku.setCategoryId(spu.getCategory3Id());
            //分类名字
            sku.setCategoryName(category.getName());
            //品牌名字
            sku.setBrandName(brand.getName());
            //增加
            skuMapper.insertSelective(sku);
        }
    }

    @Override
    public Goods findGoodsBySpuId(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skus);
        return goods;
    }

    @Override
    public void audit(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        String isDelete = spu.getIsDelete();
        if ("1".equals(isDelete)) {
            throw new RuntimeException("此商品已被删除,无法审核!");
        } else {
            spu.setStatus("1");
            spu.setIsMarketable("1");
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }

    @Override
    public void pull(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu.getIsDelete().equals("1")) {
            throw new RuntimeException("此商品已被删除，无法下架！");
        }
        spu.setIsMarketable("0");//下架状态
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void put(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu.getIsDelete().equals("1")) {
            throw new RuntimeException("此商品已被删除，无法上架！");
        }
        if (spu.getStatus().equals("0")) {
            throw new RuntimeException("此商品未通过审核，无法上架！");
        }
        spu.setIsMarketable("1");//上架状态
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public int putMany(Long[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("1");
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //批量操作
        criteria.andIn("id", Arrays.asList(ids));
        criteria.andEqualTo("isMarketable", "0"); //下架商品才能上架
        criteria.andEqualTo("status", "1"); //审核通过的
        criteria.andEqualTo("isDelete", "0"); //非删除的
        return spuMapper.updateByExampleSelective(spu,example);
    }

    @Override
    public int pullMany(Long[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("0");
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        criteria.andEqualTo("isMarketable","1");//上架商品才能下架
        return spuMapper.updateByExampleSelective(spu,example);
    }

    @Override
    public void logicDelete(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (!spu.getIsMarketable().equals("0")){
            throw new RuntimeException("必须先下架才能删除!");
        }
        spu.setIsDelete("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void delete(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (!spu.getIsDelete().equals("1")){
            throw new RuntimeException("此商品不能删除");
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void restore(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        spu.setIsDelete("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public Spu findSpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }
}
