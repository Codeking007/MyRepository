package com.changgou.goods.service;

import com.changgou.goods.pojo.Album;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface AlbumService {

    PageInfo<Album> findPage(Album album, int page, int size);

    PageInfo<Album> findPage(int page, int size);

    List<Album> findList(Album album);

    void delete(Long id);

    void update(Album album);

    void add(Album album);

    Album findById(Long id);

    List<Album> findAll();
}
