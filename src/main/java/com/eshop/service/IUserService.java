package com.eshop.service;

import com.eshop.common.ServerResponse;
import com.eshop.pojo.User;

import javax.servlet.http.HttpSession;

/**
 * Created by windvalley on 2018/7/14.
 */
public interface IUserService {
    ServerResponse<User> login(String userName, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String value, String type);

    ServerResponse<User> getUserInfo(HttpSession session);

    ServerResponse<String> getQuestion(String userName);

    ServerResponse<String> checkAnswer(String userName, String question, String answer);

    ServerResponse<String> forgetResetPassword(String userName, String password, String token);

    ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);
}
