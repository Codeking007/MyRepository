package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/weixin/pay")
@CrossOrigin
public class WeiXinPayController {
    @Value("${mq.pay.exchange.order}")
    private String exchange;
    @Value("${mq.pay.queue.order}")
    private String queue;
    @Value("${mq.pay.routing.key}")
    private String routing;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private WeiXinPayService weiXinPayService;

    @RequestMapping("/create/native")
    public Result createNative(@RequestParam Map<String, String> paramMap) {
        paramMap.put("username", "zhangsan");
        Map<String, String> resultMap = weiXinPayService.createNative(paramMap);
        return new Result(true, StatusCode.OK, "创建二维码预付订单成功!", resultMap);
    }

    @GetMapping("/status/query")
    public Result queryStatus(String out_trade_no) {
        Map<String, String> resultMap = weiXinPayService.queryPayStatus(out_trade_no);
        return new Result(true, StatusCode.OK, "查询状态成功!", resultMap);
    }

    /**
     * 微信支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/notify/url")
    public String notifyUrl(HttpServletRequest request) {
        try {
            //1.读取支付回调数据
            ServletInputStream is = request.getInputStream();
            //2.转换成字符串
            String result = IOUtils.toString(is, "UTF-8");
            //3.将xml字符串转化为Map结构
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);

            //读取附加消息-交换机与队列
            Map<String, String> attchMap = JSON.parseObject(map.get("attach"), Map.class);
            exchange = attchMap.get("exchange");
            routing = attchMap.get("routingKey");
            //发送MQ消息
            rabbitTemplate.convertAndSend(exchange, routing, JSON.toJSONString(map));

            //4.包装数据返回
            Map respMap = new HashMap();
            respMap.put("return_code", "SUCCESS");
            respMap.put("return_msg", "OK");
            return WXPayUtil.mapToXml(respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("createSeckillQueue")
    public String createSeckillQueue() {
        //发送秒杀MQ消息队列
        rabbitTemplate.convertAndSend("exchange.order", "queue.seckillorder", "{'flag':'ok'}");
        return "ok";
    }

    @RequestMapping("closePay")
    public Result closePay(Long id) {
        try {
            Map<String, String> map = weiXinPayService.closePay(id);
            return new Result(true,StatusCode.OK,"关闭支付成功!",map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,StatusCode.ERROR,"失败!");
    }
}
