package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: Steven
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MjAxMzczNzA0MSwiYXV0aG9yaXRpZXMiOlsidXNlciIsInNhbGVzbWFuIl0sImp0aSI6IjhkMmZmMjk5LWI5NzEtNDI2Zi04OGMxLTg1NjY3NjAzYmExZiIsImNsaWVudF9pZCI6ImNoYW5nZ291IiwidXNlcm5hbWUiOiJzeml0aGVpbWEifQ.gKwCoZHCKIzUr9J9V3A6-SALnbp-n8MMl-QiqQcdBF1o22EK6zhv6fXOLBn3--sv95wiqV9nvSsmxfCbpvdVVNiTRd0zMWlsjxMrhhpdrs1WIckgiyVbTHHksxB6slBDjOI77rUidUDRbAmnRExqlEOGB2vYIJ8jt5N6AryCKrjopzKQDNg3vLovqoawGX-HPa7QuL_c2asN49NDjrZavyeo20hIHmdT0Z0-WBQhIOaRvBo5bAQjgu_arrpUDDInSv64iwgFs0E1hNMBeXBpDdLkVQSUsoF6VLQ9rV4BOZpJM1sUa4lttbcyga603xNOK3lRjfZYhK_UcQ4NFbrKVw";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvFsEiaLvij9C1Mz+oyAmt47whAaRkRu/8kePM+X8760UGU0RMwGti6Z9y3LQ0RvK6I0brXmbGB/RsN38PVnhcP8ZfxGUH26kX0RK+tlrxcrG+HkPYOH4XPAL8Q1lu1n9x3tLcIPxq8ZZtuIyKYEmoLKyMsvTviG5flTpDprT25unWgE4md1kthRWXOnfWHATVY7Y/r4obiOL1mS5bEa/iNKotQNnvIAKtjBM4RlIDWMa6dmz+lHtLtqDD2LF1qwoiSIHI75LQZ/CNYaHCfZSxtOydpNKq8eb1/PGiLNolD4La2zf0/1dlcr5mkesV570NxRmU1tFm8Zd3MZlZmyv9QIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
