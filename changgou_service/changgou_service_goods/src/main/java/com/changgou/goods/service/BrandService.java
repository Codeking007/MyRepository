package com.changgou.goods.service;

import com.changgou.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BrandService {
    /**
     * 查询所有
     *
     * @return
     */
    List<Brand> findAll();

    /**
     * 根据id查询品牌
     *
     * @param id
     * @return
     */
    Brand findById(Integer id);

    /**
     * 新增单个商品
     *
     * @param brand
     */
    void add(Brand brand);

    /**
     * 修改商品
     *
     * @param brand
     */
    void update(Brand brand);

    /**
     * 删除商品
     *
     * @param id
     */
    void delete(Integer id);

    /**
     * 多条件查询品牌
     *
     * @param brand
     * @return
     */
    List<Brand> findList(Brand brand);

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findPage(int page, int size);

    /**
     * 多条件分页
     *
     * @param brand
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findPage(Brand brand, int page, int size);

    /**
     * 根据类别查询品牌
     *
     * @param cid
     * @return
     */
    List<Brand> findByCategory(Integer cid);
}
