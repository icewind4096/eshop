package com.eshop.service;

import com.eshop.common.ServerResponse;
import com.eshop.pojo.Product;

/**
 * Created by windvalley on 2018/7/18.
 */
public interface IProductService {
    public ServerResponse saveProduct(Product product);

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse managerProductDetail(Integer prodcutId);
}
