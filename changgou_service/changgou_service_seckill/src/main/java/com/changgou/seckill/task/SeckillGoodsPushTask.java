package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class SeckillGoodsPushTask {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/5 * * * * *")
    public void loadGoodsPushRedis() {
        System.out.println("定时任务被调度了...");
        //获取时间段集合
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date startTime : dateMenus) {
            //得到时间段字符串
            String extName = DateUtil.data2str(startTime, DateUtil.PATTERN_YYYYMMDDHH);
            //根据时间段查询对应的秒杀商品数据
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //商品必须审核通过
            criteria.andEqualTo("status", '1');
            //库存必须大于0
            criteria.andGreaterThan("stockCount", 0);
            //在指定的时间间隔之内
            criteria.andGreaterThanOrEqualTo("startTime", startTime);
            criteria.andLessThan("endTime", DateUtil.addDateHour(startTime, 2));
            //排除已经压入缓存的商品 Fri Feb 07 21:59:59 CST 2020
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + extName).keys();
            if (keys != null && keys.size() > 0) {
                criteria.andNotIn("id", keys);
            }
            //查询数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
            System.out.println(extName + "时段导入的商品个数为: " + seckillGoods.size());
            //将秒杀商品数据存入到redis缓存
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps("SeckillGoods_" + extName).put(seckillGood.getId(), seckillGood);
                //方式一,id存入redis
                Long[] ids = pushIds(seckillGood.getStockCount(), seckillGood.getId());
                redisTemplate.boundListOps("SeckillGoodsCountList_"+seckillGood.getId()).leftPushAll(ids);
                //方式二,使用redis的自减,先把库存存到redis
                redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGood.getId(),seckillGood.getStockCount());
            }
        }
    }

    /**
     * 将商品的id存入数组,有多少库存存多少个
     *
     * @param len
     * @param id
     * @return
     */
    public Long[] pushIds(int len, Long id) {
        Long[] ids = new Long[len];
        for (int i = 0; i < len; i++) {
            ids[i] = id;
        }
        return ids;
    }
}
