package com.changgou.order.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ScheduledTask {
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/5 * * * * ?")
    public void changeStatus() {
        //1.读取redis中以下单的orderId
//        int size = Math.toIntExact(redisTemplate.boundListOps("orders").size());
//        for (int i = 0; i < size; i++) {
//            String orderId = (String) redisTemplate.boundListOps("orders").leftPop();
//            System.out.println("定时任务-订单号: " + orderId);
//        }
        //2.调用changgou_service_pay_api 中PayFeign 传入订单号,查询状态
        //3.根据状态进行相应的操作
        //如果已支付,修改数据库状态,并删除redis中订单
        //如果等待,保持原状,等待下一次任务
        //如果失败,修改数据库中订单状态,并删除redis中订单
//        System.out.println("定时任务:" + System.currentTimeMillis());
    }
}
