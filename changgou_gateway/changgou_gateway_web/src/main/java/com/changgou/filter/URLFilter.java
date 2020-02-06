package com.changgou.filter;

/**
 * 用户识别不需要登录的url地址
 */
public class URLFilter {
    private static final String[] ignore = {
            "/api/user/login",
            "/api/user/add"
    };

    /**
     * 识别传入的uri是否要权限校验
     * @param uri
     * @return true 需要校验| false 不需要校验
     */
    public static boolean hasAuthorize(String uri) {
        for (String ig : ignore) {
            if (uri.startsWith(ig)) {
                return false;
            }
        }
        return true;
    }
}
