package com.changgou.test;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    @Test
    public void testCreateJwt(){
        //1.创建jwt构建器
        JwtBuilder jwtBuilder = Jwts.builder();
        //2.设置唯一编号
        jwtBuilder.setId("007");
        //3.设置主题,可以是JSON数据
        jwtBuilder.setSubject("测试主题");
        //4.设置签发日期
        jwtBuilder.setIssuedAt(new Date());
        //5.设置签发人
        jwtBuilder.setIssuer("www.itheima.com");
        //设置过期时间30秒
        jwtBuilder.setExpiration(new Date(System.currentTimeMillis()+60000));
        //自定义claim
        Map<String, Object> user = new HashMap<>();
        user.put("name","steven");
        user.put("age","18");
        user.put("address","深圳市.黑马程序员");
        jwtBuilder.addClaims(user);
        //6.设置签证
        jwtBuilder.signWith(SignatureAlgorithm.HS256,"itheima.steven");
        //7.生成令牌
        String token = jwtBuilder.compact();

        System.out.println(token);
    }
    @Test
    public void testParseJwt(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwMDciLCJzdWIiOiLmtYvor5XkuLvpopgiLCJpYXQiOjE1ODA2MTU0NzMsImlzcyI6Ind3dy5pdGhlaW1hLmNvbSIsImV4cCI6MTU4MDYxNTUzMywiYWRkcmVzcyI6Iua3seWcs-W4gi7pu5HpqaznqIvluo_lkZgiLCJuYW1lIjoic3RldmVuIiwiYWdlIjoiMTgifQ.iCOxUejxred0EXtIuvWT4V4aBRiz4yfXQ9JBBtHdQ_A";
        //1.创建Jwt解析器
        JwtParser jwtParser = Jwts.parser();
        //2.设置签名-密钥
        jwtParser.setSigningKey("itheima.steven");
        //3.设置要解析的密文,并读取结果
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        System.out.println(claims);
    }
}
