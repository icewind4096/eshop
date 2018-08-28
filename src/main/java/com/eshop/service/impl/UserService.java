package com.eshop.service.impl;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.controller.common.SecurityUtil;
import com.eshop.dao.UserMapper;
import com.eshop.enums.UserRoleEnum;
import com.eshop.pojo.User;
import com.eshop.service.IUserService;
import com.eshop.util.CookieUtil;
import com.eshop.util.MD5Util;
import com.eshop.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by windvalley on 2018/7/14.
 */
@Service
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * login
     * @param userName
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String userName, String password) {
        if (userMapper.checkUserName(userName) == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(userName, MD5Password);
        if (user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse validResponse = checkValid(user.getUsername(), ConstVariable.USERNAME);
        if (validResponse.isSuccess() == false){
            return validResponse;
        }

        validResponse = checkValid(user.getUsername(), ConstVariable.EMAIL);
        if (validResponse.isSuccess() == false){
            return validResponse;
        }

        user.setRole(UserRoleEnum.CUSTOMER.getCode());
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        if (userMapper.insert(user) == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String value, String type) {
        if (StringUtils.isNoneBlank() == false){
            return ServerResponse.createByErrorMessage("参数错误");
        }

        if (ConstVariable.USERNAME.equalsIgnoreCase(type) == true){
            if (userMapper.checkUserName(value) > 0){
                return ServerResponse.createByErrorMessage("用户已存在");
            }
        } else {
            if (userMapper.checkEMail(value) > 0){
                return ServerResponse.createByErrorMessage("邮箱已存在");
            }
        }
        return ServerResponse.createBySuccess("校验成功");
    }

    @Override
    public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest) {
        User user = SecurityUtil.getUserInfoByLoginToken(httpServletRequest);
        if (user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登陆, 无法获得当前用户登陆信息");
    }

    @Override
    public ServerResponse logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletRequest, httpServletResponse);
        RedisShardedPoolUtil.del(loginToken);

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<String> getQuestion(String userName) {
        if (userMapper.checkUserName(userName) == 0){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.getQuestionByUserName(userName);
        if (StringUtils.isBlank(question) == false){
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorMessage("找回密码问题的数据是空的");
    }

    @Override
    public ServerResponse<String> checkAnswer(String userName, String question, String answer) {
        if (userMapper.checkAnswer(userName, question, answer) > 0){
            String forgetToken = UUID.randomUUID().toString();
            RedisShardedPoolUtil.set(ConstVariable.TOKENPREFIX + userName, forgetToken, 60 * 60 * 12);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("用户问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String userName, String password, String token) {
        //如果token是个空白的，直接返回
        if (StringUtils.isBlank(token) == true){
            return ServerResponse.createByErrorMessage("参数错误，需要token");
        }

        if (userMapper.checkUserName(userName) == 0){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String cacheToken = RedisShardedPoolUtil.get(ConstVariable.TOKENPREFIX + userName);
        if (StringUtils.isBlank(cacheToken) == true){
            return ServerResponse.createByErrorMessage("无效的Token");
        }

        if (StringUtils.equals(cacheToken, token) == true){
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            if (userMapper.updatePasswordByUserName(userName, md5Password) > 0){
                return ServerResponse.createBySuccess("修改密码成功");
            } else {
                return ServerResponse.createByErrorMessage("修改密码失败");
            }
        } else {
            return ServerResponse.createByErrorMessage("错误的Token, 请重新获取Token");
        }
    }

    @Override
    public ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew) {
        if (userMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld)) == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        if (userMapper.updateByPrimaryKeySelective(user) > 0){
            return ServerResponse.createBySuccess("修改密码成功");
        } else {
            return ServerResponse.createByErrorMessage("修改密码失败");
        }
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        if (userMapper.checkEmailByUserId(user.getId(), user.getEmail()) > 0){
            return ServerResponse.createByErrorMessage("email已经存在，请修改EMail");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        if (userMapper.updateByPrimaryKeySelective(updateUser) > 0){
            return ServerResponse.createBySuccess("更新用户数据成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新用户数据失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user != null){
            return ServerResponse.createBySuccess("查找用户数据成功", user);
        }
        return ServerResponse.createByErrorMessage("查找用户数据失败");
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if ((user != null) && (user.getRole() == UserRoleEnum.ADMIN.getCode())){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
