package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;

public interface SpuService {
    void saveGoods(Goods goods);

    Goods findGoodsBySpuId(Long id);

    //修改状态,删除,上架
    void audit(Long id);

    //下架
    void pull(Long id);

    //上架
    void put(Long id);

    //批量上架
    int putMany(Long[] ids);

    //批量下架
    int pullMany(Long[] ids);

    void logicDelete(Long id);

    void delete(Long id);

    void restore(Long id);

    Spu findSpuById(Long id);
}
