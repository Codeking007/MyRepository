package com.changgou.goods.service.impl;

import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuMapper skuMapper;

    @Override
    public List<Sku> findList(Sku sku) {
        return skuMapper.select(sku);
    }

    @Override
    public Sku findById(Long id) {
        return skuMapper.selectByPrimaryKey(id);
    }
}
