package com.changgou.goods.feign;

import com.changgou.goods.pojo.Goods;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "goods")
@RequestMapping("spu")
public interface SpuFeign {
    @GetMapping("/goods/{id}")
    Result<Goods> findById(@PathVariable Long id);
}
