package com.eshop.controller.common;

import com.eshop.pojo.User;
import com.eshop.util.CookieUtil;
import com.eshop.util.JSONUtil;
import com.eshop.util.RedisShardedPoolUtil;
import com.github.pagehelper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by windvalley on 2018/8/28.
 */
public class SecurityUtil {
    public static User getUserInfoByLoginToken(HttpServletRequest httpServletRequest){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtil.isEmpty(loginToken) == false){
            String userJSON = RedisShardedPoolUtil.get(loginToken);
            return JSONUtil.string2Object(userJSON, User.class);
        }
        return null;
    }
}
