package com.changgou.goods.controller;

import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
@CrossOrigin
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping
    public Result<List<Brand>> findAll() {
        List<Brand> brands = brandService.findAll();
        return new Result<List<Brand>>(true, StatusCode.OK, "查询品牌信息成功", brands);
    }

    @GetMapping("/{id}")
    public Result<Brand> findById(@PathVariable Integer id) {
        Brand brand = brandService.findById(id);
        return new Result<Brand>(true, StatusCode.OK, "查询品牌成功", brand);
    }

    @PostMapping
    public Result add(@RequestBody Brand brand) {
        brandService.add(brand);
        return new Result(true, StatusCode.OK, "保存品牌成功");
    }

    @PutMapping
    public Result update(@RequestBody Brand brand) {
        brandService.update(brand);
        return new Result(true, StatusCode.OK, "修改商品成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        brandService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    @PostMapping("/search")
    public Result<List<Brand>> findList(@RequestBody(required = false) Brand brand) {
        List<Brand> list = brandService.findList(brand);
        return new Result<List<Brand>>(true, StatusCode.OK, "查询成功", list);
    }

    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size) {
        PageInfo<Brand> pageInfo = brandService.findPage(page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) Brand brand, @PathVariable int page, @PathVariable int size) {
        PageInfo<Brand> pageInfo = brandService.findPage(brand, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    @GetMapping("/category/{cid}")
    public Result<List<Brand>> findByCategory(@PathVariable Integer cid) {
        List<Brand> brands = brandService.findByCategory(cid);
        return new Result<>(true, StatusCode.OK, "查询成功", brands);
    }
}
