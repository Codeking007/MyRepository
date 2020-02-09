package com.changgou.pay.service;

import java.util.Map;

public interface WeiXinPayService {

    /**
     * out_trade_no 订单号,
     * total_fee 金额(分),
     * exchange 交换机,
     * routingKey 路由Key
     *
     * @param paramMap
     * @return
     */
    Map createNative(Map<String, String> paramMap);

    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    Map queryPayStatus(String out_trade_no);

    /**
     * 关闭支付
     */
    Map<String, String> closePay(Long orderId) throws Exception;
}
