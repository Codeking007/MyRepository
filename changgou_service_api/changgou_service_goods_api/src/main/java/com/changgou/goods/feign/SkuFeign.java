package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {
    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable String status);

    @GetMapping("/{id}")
    Result<Sku> findById(@PathVariable Long id);

    @PostMapping("/decr/count/{username}")
    Result decrCount(@PathVariable String username);

    @PostMapping("/incr/count/{username}")
    Result incrCount(@PathVariable String username);
}
