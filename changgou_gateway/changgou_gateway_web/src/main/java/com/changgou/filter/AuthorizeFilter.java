package com.changgou.filter;

import com.changgou.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    //令牌的key
    private static final String AUTHORIZE_TOKEN = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取请求的uri
        String uri = request.getURI().getPath();
        //登录放行
        if (uri.startsWith("/api/user/login")) {
            return chain.filter(exchange);
        } else {//非登录请求
            String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
            if (StringUtils.isEmpty(token)) {
                token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
            }
            if (StringUtils.isEmpty(token)) {
                HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
                if (cookie != null) {
                    token = cookie.getValue();
                }
            }
            //如果还是没有则返回405错误
            if (StringUtils.isEmpty(token)) {
                response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
                return response.setComplete();
            } else {//如果有token
                try {
//                    Claims claims = JwtUtil.parseJWT(token);
                    //解析成功,把令牌返回
                    request.mutate().header(AUTHORIZE_TOKEN, "bearer" + token);
                } catch (Exception e) {
                    e.printStackTrace();
                    //无效的认证
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }
                return chain.filter(exchange);
            }
        }
    }

    /**
     * 过滤器执行顺序设置
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
