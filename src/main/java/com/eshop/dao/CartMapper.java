package com.eshop.dao;

import com.eshop.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int deleteCartByUserIdAndProducts(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

    int checkStatusCartByUserIdAndProducts(@Param("userId") Integer userId, @Param("status") Integer status, @Param("productIdList") List<String> productIdList);

    int selectCartProductCount(@Param("userId") Integer userId);
}