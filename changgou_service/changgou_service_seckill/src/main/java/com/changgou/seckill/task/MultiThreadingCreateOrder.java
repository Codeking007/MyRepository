package com.changgou.seckill.task;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.utils.SeckillStatus;
import entity.IdWorker;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MultiThreadingCreateOrder {
    @Autowired
    private Environment env;
    @Autowired
    private RedisTemplate redisTemplate;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Async
    public void createOrder() {
//        try {
//            System.out.println("开始查询下单相关业务,模拟业务处理时间...");
//            Thread.sleep(2000);
//            System.out.println("完成查询下单相关业务,模拟业务处理时间...");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //从队列中获取排队信息-左进右出
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();
        //1.超卖方式:队列只能处理超卖问题,不能解决正确库存显示问题
//        Object sgoods = redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId()).rightPop();
        //如果队列没数据,说明没有库存了
//        if (sgoods==null) {
//            //清理排队标识
//            redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());
//            //清理抢单标识
//            redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStatus.getUsername());
//            return;
//        }
        //2.方式
        Long count = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillStatus.getGoodsId(), -1);

        if (seckillStatus != null) {
            String time = seckillStatus.getTime();
            String username = seckillStatus.getUsername();
            Long id = seckillStatus.getGoodsId();
            //根据id查询redis中秒杀商品
            SeckillGoods goods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(id);
            if (goods == null || count <= 0) {
                throw new RuntimeException("你来晚了一步,商品被抢购一空!");
            }
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());
            seckillOrder.setSeckillId(id);
            seckillOrder.setMoney(goods.getCostPrice());
            seckillOrder.setUserId(username);
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setStatus("0");
            //将秒杀订单存入到Redis中
            redisTemplate.boundHashOps("SeckillOrder").put(username, seckillOrder);

            goods.setStockCount(count.intValue());

            if (count <= 0) {
                seckillGoodsMapper.updateByPrimaryKeySelective(goods);
                redisTemplate.boundHashOps("SeckillGoods_" + time).delete(id);
            } else {
                redisTemplate.boundHashOps("SeckillGoods_" + time).put(id, goods);
            }

            //抢单成功,更新抢单状态,排队--等待支付
            seckillStatus.setStatus(2);//1:排队中，2:秒杀等待支付,3:支付超时，4:秒杀失败,5:支付完成
            seckillStatus.setOrderId(seckillOrder.getId());  //更新订单id
            seckillStatus.setMoney(new Float(seckillOrder.getMoney()));  //记录金额
            //更新用户订单状态为等待支付
            redisTemplate.boundHashOps("UserQueueStatus").put(username, seckillStatus);

            //延时队列
            sendTimerMessage(seckillStatus);
        }
    }

    public void sendTimerMessage(SeckillStatus seckillStatus){
        rabbitTemplate.convertAndSend(env.getProperty("mq.pay.queue.seckillordertimerdelay"), (Object) JSON.toJSONString(seckillStatus), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("10000");
                return message;
            }
        });
    }
}
