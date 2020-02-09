package com.changgou.goods.dao;

import com.changgou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> {
    @Update("UPDATE tb_sku SET num = num - #{num},sale_num = sale_num + #{num} WHERE id = #{skuId} AND num > #{num}")
    int decrCount(@Param("num") Integer num, @Param("skuId") Long skuId);

    @Update("UPDATE tb_sku SET num = num + #{num},sale_num = sale_num - #{num} WHERE id = #{skuId}")
    int incrCount(@Param("num") Integer num, @Param("skuId") Long skuId);
}
