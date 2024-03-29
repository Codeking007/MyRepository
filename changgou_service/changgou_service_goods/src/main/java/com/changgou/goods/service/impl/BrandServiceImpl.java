package com.changgou.goods.service.impl;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Brand brand) {
        //与insert的区别在于,这个只会插入有值的字段
        brandMapper.insertSelective(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Brand> findList(Brand brand) {
        Example example = createExample(brand);
        return brandMapper.selectByExample(example);
    }

    @Override
    public PageInfo<Brand> findPage(int page, int size) {
        //设置分页条件
        PageHelper.startPage(page, size);
        //查询数据
        List<Brand> brands = brandMapper.selectAll();
        //返回数据
        return new PageInfo<Brand>(brands);
    }

    @Override
    public PageInfo<Brand> findPage(Brand brand, int page, int size) {
        Example example = createExample(brand);
        PageHelper.startPage(page, size);
        List<Brand> brands = brandMapper.selectByExample(example);
        return new PageInfo<Brand>(brands);
    }

    @Override
    public List<Brand> findByCategory(Integer cid) {
        return brandMapper.findByCategory(cid);
    }

    /**
     * 品牌查询条件构建方法
     *
     * @param brand
     * @return
     */
    private Example createExample(Brand brand) {
        //构建查询条件
        Example example = new Example(Brand.class);
        //创建条件构建器
        Example.Criteria criteria = example.createCriteria();
        if (brand != null) {
            //品牌名称
            if (StringUtils.isNotEmpty(brand.getName())) {
                criteria.andLike("name", "%" + brand.getName() + "%");
            }
            //品牌首字母
            if (StringUtils.isNotEmpty(brand.getLetter())) {
                criteria.andEqualTo("letter", brand.getLetter());
            }
        }
        return example;
    }
}
