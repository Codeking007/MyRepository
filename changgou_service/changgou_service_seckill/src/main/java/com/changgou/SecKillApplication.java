package com.changgou;

import entity.IdWorker;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan(basePackages = {"com.changgou.seckill.dao"})
@EnableScheduling//开启定时任务
@EnableAsync//开启异步功能
public class SecKillApplication {
    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(SecKillApplication.class, args);
    }

    @Bean
    public IdWorker idWork() {
        return new IdWorker(0, 2);
    }

    /**
     * 到期数据队列
     *
     * @return
     */
    @Bean
    public Queue seckillOrderTimeQueue() {
        return new Queue(env.getProperty("mq.pay.queue.seckillordertimer"), true);
    }

    /**
     * 超时数据队列
     *
     * @return
     */
    @Bean
    public Queue delaySeckillOrderTimerQueue() {
        return QueueBuilder.durable(env.getProperty("mq.pay.queue.seckillordertimerdelay"))
                .withArgument("x-dead-letter-exchange", env.getProperty("mq.pay.exchange.order"))        // 消息超时进入死信队列，绑定死信队列交换机
                .withArgument("x-dead-letter-routing-key", env.getProperty("mq.pay.queue.seckillordertimer"))   // 绑定指定的routing-key
                .build();
    }

    @Bean
    public Binding basicBinding() {
        return BindingBuilder.bind(seckillOrderTimeQueue()).to(basicExchange()).with(env.getProperty("mq.pay.queue.seckillordertimer"));
    }

    @Bean
    public DirectExchange basicExchange() {
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"));
    }
}
