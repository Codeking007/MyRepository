package com.changgou.goods.service.impl;

import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.TemplateMapper;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Template;
import com.changgou.goods.service.TemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageInfo<Template> findPage(Template template, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = getExample(template);
        return new PageInfo<Template>(templateMapper.selectByExample(example));
    }

    @Override
    public PageInfo<Template> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return new PageInfo<Template>(templateMapper.selectAll());
    }

    @Override
    public List<Template> findList(Template template) {
        Example example = getExample(template);
        List<Template> templates = templateMapper.selectByExample(example);
        return templates;
    }

    @Override
    public void delete(Integer id) {
        templateMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Template template) {
        templateMapper.updateByPrimaryKey(template);
    }

    @Override
    public void add(Template template) {
        templateMapper.insert(template);
    }

    @Override
    public Template findById(Integer id) {
        return templateMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Template> findAll() {
        return templateMapper.selectAll();
    }

    @Override
    public Template findByCategoryId(Integer id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        return templateMapper.selectByPrimaryKey(category.getTemplateId());
    }

    public Example getExample(Template template){
        Example example = new Example(Template.class);
        Example.Criteria criteria = example.createCriteria();
        if (template!=null){
            if (template.getId()!=null){
                criteria.andEqualTo("id",template.getId());
            }
            if (template.getName()!=null){
                criteria.andLike("name","%"+template.getName()+"%");
            }
            if (!StringUtils.isEmpty(template.getSpecNum())){
                criteria.andEqualTo("specNum",template.getSpecNum());
            }
            if (!StringUtils.isEmpty(template.getParaNum())){
                criteria.andEqualTo("paraNum",template.getParaNum());
            }
        }
        return example;
    }
}
