package com.changgou.goods.controller;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.service.SpuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spu")
@CrossOrigin
public class SpuController {
    @Autowired
    private SpuService spuService;

    @PostMapping("/save")
    public Result save(@RequestBody Goods goods) {
        spuService.saveGoods(goods);
        return new Result(true, StatusCode.OK, "保存成功");
    }

    @GetMapping("/goods/{id}")
    public Result findGoodsById(@PathVariable Long id) {
        Goods goods = spuService.findGoodsBySpuId(id);
        return new Result(true, StatusCode.OK, "查询成功", goods);
    }

    @PutMapping("/audit/{id}")
    public Result audit(@PathVariable Long id) {
        spuService.audit(id);
        return new Result(true, StatusCode.OK, "审核成功");
    }

    @PutMapping("/pull/{id}")
    public Result pull(@PathVariable Long id) {
        spuService.pull(id);
        return new Result(true, StatusCode.OK, "下架成功");
    }

    @PutMapping("/put/{id}")
    public Result put(@PathVariable Long id) {
        spuService.put(id);
        return new Result(true, StatusCode.OK, "上架成功");
    }

    @PostMapping("/putMany")
    public Result putMany(@RequestBody Long[] ids) {
        int count = spuService.putMany(ids);
        return new Result(true, StatusCode.OK, "上架" + count + "个商品");
    }

    @PostMapping("/pullMany")
    public Result pullMany(@RequestBody Long[] ids) {
        int count = spuService.pullMany(ids);
        return new Result(true, StatusCode.OK, "下架" + count + "个商品");
    }

    @DeleteMapping("/logic/delete/{id}")
    public Result logicDelete(@PathVariable Long id){
        spuService.logicDelete(id);
        return new Result(true,StatusCode.OK,"逻辑删除成功!");
    }

    @PutMapping("/restore/{id}")
    public Result restore(@PathVariable Long id){
        spuService.restore(id);
        return new Result(true,StatusCode.OK,"数据恢复成功!");
    }
}
