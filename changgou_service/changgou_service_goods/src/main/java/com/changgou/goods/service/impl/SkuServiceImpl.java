package com.changgou.goods.service.impl;

import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.service.SkuService;
import com.changgou.order.pojo.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Sku> findList(Sku sku) {
        return skuMapper.select(sku);
    }

    @Override
    public Sku findById(Long id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    @Override
    public void decrCount(String username) {
        List<OrderItem> orderItems = redisTemplate.boundHashOps("Cart_" + username).values();
        for (OrderItem orderItem : orderItems) {
            System.out.println("ID: " + orderItem.getSkuId() + "库存减少: " + orderItem.getNum());
            int count = skuMapper.decrCount(orderItem.getNum(), orderItem.getSkuId());
            if (count < 0) {
                throw new RuntimeException("库存不足,无法购买!");
            }
        }
    }

    @Override
    public void incrCount(String username) {
        List<OrderItem> orderItems = redisTemplate.boundHashOps("Cart_" + username).values();
        for (OrderItem orderItem : orderItems) {
            int count = skuMapper.incrCount(orderItem.getNum(), orderItem.getSkuId());
        }
    }
}
