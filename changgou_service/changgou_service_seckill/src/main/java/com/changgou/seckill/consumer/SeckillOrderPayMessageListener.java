package com.changgou.seckill.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SeckillOrderPayMessageListener {
    @Autowired
    private SeckillOrderService seckillOrderService;

    @RabbitListener(queues = "${mq.pay.queue.seckillorder}")
    public void payListener(String msg) {
        Map<String, String> result = JSON.parseObject(msg, Map.class);
        System.out.println("收到消息,参数为: " + result);
        String return_code = result.get("return_code");

        if ("success".equalsIgnoreCase(return_code)) {
            //业务结果
            String result_code = result.get("result_code");
            //获取订单号
            String out_trade_no = result.get("out_trade_no");
            //交易流水号
            String transaction_id = result.get("transaction_id");

            //附加参数
            Map<String, String> attachMap = JSON.parseObject(result.get("attach"), Map.class);
            //用户名
            String username = attachMap.get("username");
            if ("success".equalsIgnoreCase(result_code)) {
                //修改订单状态
                seckillOrderService.updatePayStatus(out_trade_no,transaction_id,username);
            } else {
                //订单删除
                seckillOrderService.closeOrder(username);
            }
        }
    }
}
