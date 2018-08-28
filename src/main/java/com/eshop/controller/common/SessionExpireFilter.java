package com.eshop.controller.common;

import com.eshop.common.ConstVariable;
import com.eshop.pojo.User;
import com.eshop.util.CookieUtil;
import com.eshop.util.JSONUtil;
import com.eshop.util.RedisPoolUtil;
import com.github.pagehelper.util.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by windvalley on 2018/8/28.
 */
public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtil.isNotEmpty(loginToken) == true){
            String userJSON = RedisPoolUtil.get(loginToken);
            User user = JSONUtil.string2Object(userJSON, User.class);
            if (user != null){
                RedisPoolUtil.expire(loginToken, ConstVariable.RedisCache.REDIS_SESSION_EXTIME);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
