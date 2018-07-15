package com.eshop.dao;

import com.eshop.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String userName);

    User selectLogin(@Param("userName") String userName, @Param("password") String md5Password);

    int checkEMail(String value);

    String getQuestionByUserName(String userName);

    int checkAnswer(@Param("userName") String userName, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByUserName(@Param("userName") String userName, @Param("password") String md5Password);

    int checkPassword(@Param("userId") Integer userId, @Param("password") String password);

    int checkEmailByUserId(@Param("userId") Integer userId, @Param("email") String email);
}