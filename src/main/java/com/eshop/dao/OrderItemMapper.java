package com.eshop.dao;

import com.eshop.pojo.Order;
import com.eshop.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> getListByOrderNoAndUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);

    void insertOrderItemList(@Param("orderItemList") List<OrderItem> orderItemList);
}