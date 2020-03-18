package com.changgou.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.security.KeyPair;

/**
 * spring security Oauth2 授权服务器配置
 */
@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    //数据源，用于从数据库获取数据进行认证操作，测试可以从内存中获取
    @Autowired
    private DataSource dataSource;
    //jwt令牌转换器
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    //SpringSecurity 用户自定义授权认证类
    @Autowired
    UserDetailsService userDetailsService;
    //授权认证管理器
    @Autowired
    AuthenticationManager authenticationManager;
    //令牌持久化存储接口
    @Autowired
    TokenStore tokenStore;
    @Autowired
    private CustomUserAuthenticationConverter customUserAuthenticationConverter;

    /***
     * 第一步:配置授权服务,首先要知道客户端信息从哪读取所以要配,客户端信息配置:配置客户端详情服务,客户端id、密钥,限制那些客户端能访问授权服务
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /*clients.inMemory()//配在内存
                .withClient("changgou")          //客户端id
                .secret("changgou")                      //秘钥
                .redirectUris("http://localhost")       //重定向地址,验证回调地址
                .accessTokenValiditySeconds(60)          //访问令牌有效期
                .refreshTokenValiditySeconds(60)         //刷新令牌有效期
                .authorizedGrantTypes(
                        "authorization_code",          //根据授权码生成令牌
                        "client_credentials",          //客户端认证
                        "refresh_token",                //刷新令牌
                        "password")                     //密码方式认证
                .scopes("app");                         //客户端范围，名称自定义，必填*/
        //从数据库加载信息
        clients.jdbc(dataSource).clients(clientDetails());
    }

    /***
     * 要颁发token必须定义token相关配置,知道token如何存取,以及客户端支持的token,  授权服务器端点配置:配置令牌访问端点和令牌服务,怎么发放怎么存储令牌
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.accessTokenConverter(jwtAccessTokenConverter)//使用jwt方式生成令牌
                .authenticationManager(authenticationManager)//认证管理器
                .tokenStore(tokenStore)                       //令牌存储
                .userDetailsService(userDetailsService);     //用户信息service
    }

    /***
     * 既然暴露了endpoint,那就需要对endpoint定义一些安全约束授权服务器的安全配置,相当于spring security的webSecurityConfig
     * @param oauthServer
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients()//运行表单认证
                .passwordEncoder(new BCryptPasswordEncoder())
                .tokenKeyAccess("permitAll()")//公开公有密钥端点,如果你使用JWT令牌
                .checkTokenAccess("isAuthenticated()");//检测令牌
    }


    //读取密钥的配置
    @Bean("keyProp")
    public KeyProperties keyProperties(){
        return new KeyProperties();
    }

    @Resource(name = "keyProp")
    private KeyProperties keyProperties;

    //客户端配置
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    //令牌配置--令牌存储方法
    @Bean
    @Autowired
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    /****
     * JWT令牌转换器--令牌产生的方式配置
     * @param customUserAuthenticationConverter
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(CustomUserAuthenticationConverter customUserAuthenticationConverter) {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyPair keyPair = new KeyStoreKeyFactory(
                keyProperties.getKeyStore().getLocation(),                          //证书路径 changgou.jks
                keyProperties.getKeyStore().getSecret().toCharArray())              //证书秘钥 changgouapp
                .getKeyPair(
                        keyProperties.getKeyStore().getAlias(),                     //证书别名 changgou
                        keyProperties.getKeyStore().getPassword().toCharArray());   //证书密码 changgou
        converter.setKeyPair(keyPair);
        //配置自定义的CustomUserAuthenticationConverter
        DefaultAccessTokenConverter accessTokenConverter = (DefaultAccessTokenConverter) converter.getAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(customUserAuthenticationConverter);
        return converter;
    }
}

