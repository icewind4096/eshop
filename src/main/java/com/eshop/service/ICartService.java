package com.eshop.service;

import com.eshop.common.ServerResponse;

/**
 * Created by windvalley on 2018/7/26.
 */
public interface ICartService {
    public ServerResponse add(Integer userId, Integer productId, Integer count);
}
