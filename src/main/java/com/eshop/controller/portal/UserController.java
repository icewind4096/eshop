package com.eshop.controller.portal;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.User;
import com.eshop.service.IUserService;
import com.eshop.util.CookieUtil;
import com.eshop.util.JSONUtil;
import com.eshop.util.RedisPoolUtil;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;

/**
 * Created by windvalley on 2018/7/14.
 */

@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService userService;

    /**
     * 用户登录
     * @param userName
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(HttpServletResponse httpServletResponse, String userName, String password, HttpSession session){
        ServerResponse<User> response = userService.login(userName, password);
        if (response.isSuccess() == true){
//            session.setAttribute(ConstVariable.CURRENTUSER, response.getData());
            CookieUtil.writeLoginToken(httpServletResponse, session.getId());
            RedisPoolUtil.set(session.getId(), JSONUtil.object2String(response.getData()), ConstVariable.RedisCache.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 用户登出
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return userService.logout(httpServletRequest, httpServletResponse);
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return userService.register(user);
    }

    /**
     * 数据校验
     * @param value
     * @param type
     *        USERNAME
     *        EMAIL
     * @return
     */
    @RequestMapping(value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String value, String type){
        return userService.checkValid(value, type);
    }

    /**
     * 根据请求的request中的cook中的cookName->sessionId->userJSONString->user object
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest){
        return userService.getUserInfo(httpServletRequest);
    }

    /**
     * 得到用户密码提示问题
     * @param userName
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getQuestion(String userName){
        return userService.getQuestion(userName);
    }

    /**
     * 校验用户密码问题的答案，正确返回token
     * @param userName
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkAnswer(String userName, String question, String answer){
        return userService.checkAnswer(userName, question, answer);
    }

    /**
     * 忘记密码，重置密码
     * @param userName
     * @param password
     * @param token
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String userName, String password, String token){
        return userService.forgetResetPassword(userName, password, token);
    }

    /**
     * 重置密码
     * @param session
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return userService.resetPassword(user, passwordOld, passwordNew);
    }

    /**
     * 修改用户数据
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session, User user){
        User currentUser = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }

        //防止篡改，导致越权
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> serverResponse = userService.updateInformation(user);
        if (serverResponse.isSuccess() == true){
            serverResponse.getData().setUsername(currentUser.getUsername());
            session.setAttribute(ConstVariable.CURRENTUSER, serverResponse.getData());
        }
        return serverResponse;
    }

    /**
     * 查找用户数据
     * @param session
     * @return
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "未登录， 请先登录");
        }
        return userService.getInformation(user.getId());
    }
}
