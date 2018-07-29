package com.eshop.service;

import com.eshop.common.ServerResponse;
import com.eshop.pojo.Shipping;

/**
 * Created by windvalley on 2018/7/29.
 */
public interface IShippingService {
    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse delete(Integer userId, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse select(Integer userId, Integer shippingId);

    ServerResponse list(Integer userId, Integer pageNum, Integer pageSize);
}
