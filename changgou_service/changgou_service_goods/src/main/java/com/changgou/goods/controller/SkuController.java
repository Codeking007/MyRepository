package com.changgou.goods.controller;

import com.changgou.goods.pojo.Sku;
import com.changgou.goods.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sku")
@CrossOrigin
public class SkuController {
    @Autowired
    private SkuService skuService;

    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable String status) {
        Sku sku = new Sku();
        sku.setStatus(status);
        List<Sku> skus = skuService.findList(sku);
        return new Result<>(true, StatusCode.OK, "查询成功", skus);
    }
    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id){
        Sku sku = skuService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",sku);
    }
    @PostMapping("decr/count/{username}")
    public Result decrCount(@PathVariable String username){
        skuService.decrCount(username);
        return new Result(true,StatusCode.OK,"库存递减成功!");
    }
}
