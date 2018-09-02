package com.eshop.controller.portal;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.controller.common.SecurityUtil;
import com.eshop.enums.ResponseEnum;
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
 * 使用spring session 实现单点登录
 * Created by windvalley on 2018/7/14.
 */

@Controller
@RequestMapping("/user/springsession/")
public class UserSpringSessionController {
    @Autowired
    private IUserService userService;

    /**
     * 用户登录
     * @param userName
     * @param password
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(HttpSession httpSession, String userName, String password){
        int i = 0;

        int j = 123/ i;

        ServerResponse<User> response = userService.login(userName, password);
        if (response.isSuccess() == true){
            httpSession.setAttribute(ConstVariable.CURRENTUSER, response.getData());
        }
        return response;
    }

    /**
     * 用户登出
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession httpSession){
        httpSession.removeAttribute(ConstVariable.CURRENTUSER);
        return ServerResponse.createBySuccess();
    }

    /**
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession httpSession){
        User user = (User) httpSession.getAttribute(ConstVariable.CURRENTUSER);
        return ServerResponse.createBySuccess(user);
    }
}
