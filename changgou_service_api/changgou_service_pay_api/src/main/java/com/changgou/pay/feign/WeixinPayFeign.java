package com.changgou.pay.feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("pay")
@RequestMapping("/weixin/pay")
public interface WeixinPayFeign {
    @RequestMapping("closePay")
    Result closePay(Long id);
}
