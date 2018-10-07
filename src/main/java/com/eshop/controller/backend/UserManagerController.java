package com.eshop.controller.backend;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.enums.UserRoleEnum;
import com.eshop.pojo.User;
import com.eshop.service.IUserService;
import com.eshop.util.CookieUtil;
import com.eshop.util.JSONUtil;
import com.eshop.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by windvalley on 2018/7/15.
 */
@Controller
@RequestMapping("/manager/user/")
public class UserManagerController {
    @Autowired
    private IUserService userService;

    /**
     * 系统管理员登录
     * @param httpServletResponse
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(HttpServletResponse httpServletResponse, String userName, String password, HttpSession session){
        ServerResponse<User> response = userService.login(userName, password);
        if (response.isSuccess() == true){
            if (response.getData().getRole() != UserRoleEnum.ADMIN.getCode()){
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
            CookieUtil.writeLoginToken(httpServletResponse, session.getId());
            RedisShardedPoolUtil.set(session.getId(), JSONUtil.object2String(response.getData()), ConstVariable.RedisCache.REDIS_SESSION_EXTIME);
        }
        return response;
    }
}
