package com.changgou.goods.controller;

import com.changgou.goods.pojo.Template;
import com.changgou.goods.service.TemplateService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/template")
@CrossOrigin
public class TemplateController {
    @Autowired
    private TemplateService templateService;

    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Template>> findPage(@RequestBody(required = false) Template template, @PathVariable int page, @PathVariable int size) {
        PageInfo<Template> pageInfo = templateService.findPage(template, page, size);
        return new Result<PageInfo<Template>>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Template>> findPage(@PathVariable int page, @PathVariable int size) {
        PageInfo<Template> pageInfo = templateService.findPage(page, size);
        return new Result<PageInfo<Template>>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    @PostMapping("/search")
    public Result<List<Template>> findList(@RequestBody(required = false) Template template) {
        List<Template> list = templateService.findList(template);
        return new Result<List<Template>>(true, StatusCode.OK, "查询成功", list);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable int id) {
        templateService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    @PutMapping("/{id}")
    public Result update(@RequestBody Template template, @PathVariable int id) {
        template.setId(id);
        templateService.update(template);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    @PostMapping
    public Result add(@RequestBody Template template) {
        templateService.add(template);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @GetMapping("/{id}")
    public Result<Template> findById(@PathVariable int id) {
        Template template = templateService.findById(id);
        return new Result<Template>(true, StatusCode.OK, "查询成功", template);
    }

    @GetMapping
    public Result<List<Template>> findAll() {
        List<Template> all = templateService.findAll();
        return new Result<List<Template>>(true, StatusCode.OK, "查询成功", all);
    }

    @GetMapping("/category/{id}")
    public Result<Template> findByCategoryId(@PathVariable Integer id){
        Template template = templateService.findByCategoryId(id);
        return new Result<>(true,StatusCode.OK,"查询成功",template);
    }
}
