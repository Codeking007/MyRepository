package com.changgou.goods.controller;

import com.changgou.goods.pojo.Album;
import com.changgou.goods.service.AlbumService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/album")
@CrossOrigin//用来解决js跨域问题
public class AlbumController {
    @Autowired
    private AlbumService albumService;

    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Album>> findPage(@RequestBody(required = false) Album album, @PathVariable int page, @PathVariable int size) {
        PageInfo<Album> pageInfo = albumService.findPage(album, page, size);
        return new Result<PageInfo<Album>>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Album>> findPage(@PathVariable int page, @PathVariable int size) {
        PageInfo<Album> pageInfo = albumService.findPage(page, size);
        return new Result<PageInfo<Album>>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    @PostMapping("/search")
    public Result<List<Album>> findList(@RequestBody(required = false) Album album) {
        List<Album> albums = albumService.findList(album);
        return new Result<List<Album>>(true, StatusCode.OK, "查询成功", albums);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        albumService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    @PutMapping("/{id}")
    public Result update(@RequestBody Album album,@PathVariable Long id) {
        album.setId(id);
        albumService.update(album);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    @PostMapping
    public Result add(@RequestBody Album album) {
        albumService.add(album);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        Album album = albumService.findById(id);
        return new Result(true, StatusCode.OK, "查询成功", album);
    }

    @GetMapping
    public Result<List<Album>> findAll() {
        List<Album> all = albumService.findAll();
        return new Result<>(true, StatusCode.OK, "查询成功", all);
    }
}
