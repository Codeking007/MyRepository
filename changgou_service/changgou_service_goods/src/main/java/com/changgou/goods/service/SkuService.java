package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Sku;

import java.util.List;

public interface SkuService {
    List<Sku> findList(Sku sku);

    Sku findById(Long id);
}
