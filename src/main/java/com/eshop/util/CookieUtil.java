package com.eshop.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by windvalley on 2018/8/27.
 */
@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".eshop.com";       //有权限隔离的作用
    private final static String COOKIE_NAME = "eshop_login_token";

    //a suit: A.eshop.com                       cookie:domain=A.eshop.com;path="/"
    //b suit: A.eshop.com/test                  cookie:domain=A.eshop.com;path="/test"
    //c suit: A.eshop.com/test/cc               cookie:domain=A.eshop.com;path="/test/cc"
    //d suit: A.eshop.com/test/dd               cookie:domain=A.eshop.com;path="/test/dd"
    //e suit: A.eshop.com                       cookie:domain=B.eshop.com;path="/"
    private static void addCookie(HttpServletResponse response, String name, String value, String doMain, String path, int maxAge){
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);                       //禁止脚本获取cookie信息
        cookie.setDomain(doMain);
        cookie.setPath(path);                           //代表设置在根目录
                                                        //如果设置为test, 则只有test目录或者test子目录下的页面可以访问该cookie
        cookie.setMaxAge(maxAge);                       //单位是秒, 0： 删除cook， -1： 永久有效, 如果不设置，Cookie不会写入硬盘，只写在内存中，只在当前页面有效
        log.info("write cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());

        response.addCookie(cookie);
    }

    public static void writeLoginToken(HttpServletResponse response, String token){
        addCookie(response, COOKIE_NAME, token, COOKIE_DOMAIN, "/", 60 * 30);
    }

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                log.info("read cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME) == true){
                    log.info("return cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME) == true){
                    addCookie(response, cookie.getName(), cookie.getValue(), COOKIE_DOMAIN, "/", 0);
                    return;
                }
            }
        }
    }
}
