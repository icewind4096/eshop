package com.eshop.service;

import com.eshop.common.ServerResponse;
import com.eshop.vo.CartVO;

/**
 * Created by windvalley on 2018/7/26.
 */
public interface ICartService {
    ServerResponse add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVO> delete(Integer userId, String productIds);

    ServerResponse<CartVO> list(Integer userId);

    ServerResponse<CartVO> checkStatus(Integer userId, Integer status, String productIds);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
