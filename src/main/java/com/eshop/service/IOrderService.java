package com.eshop.service;

import com.eshop.common.ServerResponse;
import com.eshop.vo.OrderProductVO;

import java.util.Map;

/**
 * Created by windvalley on 2018/8/5.
 */
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse alipayCallBack(Map<String, String> paramaters);

    ServerResponse queryOrderPayStatus(Integer id, Long orderNo);

    ServerResponse<Object> create(Integer userId, Integer shippingId);

    ServerResponse cancel(Integer userId, Long orderNo);

    ServerResponse<OrderProductVO> getOrderCateProduct(Integer userId);
}
