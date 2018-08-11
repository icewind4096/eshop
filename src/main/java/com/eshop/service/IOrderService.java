package com.eshop.service;

import com.eshop.common.ServerResponse;
import com.eshop.vo.OrderProductVO;
import com.eshop.vo.OrderVO;
import com.github.pagehelper.PageInfo;

import java.awt.print.Pageable;
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

    ServerResponse<OrderVO> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    ServerResponse<PageInfo> managerList(int pageNum, int pageSize);

    ServerResponse<OrderVO> managerDetail(Long orderNo);

    ServerResponse<PageInfo> managerSearch(Long orderNo, int pageNum, int pageSize);

    ServerResponse<String> managerSendGoods(Long orderNo);
}
