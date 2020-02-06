package com.changgou.order.controller;

import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @RequestMapping("add")
    public Result add(Integer num, Long id) {
//        String username = "szitheima";
        String username = TokenDecode.getUserInfo().get("username");
        cartService.add(num, id, username);
        return new Result(true, StatusCode.OK, "加入购物车成功!");
    }

    @GetMapping("list")
    public Result list() {
//        String username = "szitheima";
        String username = TokenDecode.getUserInfo().get("username");
        List<OrderItem> orderItems = cartService.list(username);
        return new Result(true, StatusCode.OK, "购物车列表查询成功!", orderItems);
    }
}
