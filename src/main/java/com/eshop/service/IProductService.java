package com.eshop.service;

import com.eshop.common.ServerResponse;
import com.eshop.pojo.Product;
import com.eshop.vo.ProductDetailVO;
import com.github.pagehelper.PageInfo;

/**
 * Created by windvalley on 2018/7/18.
 */
public interface IProductService {
    public ServerResponse saveProduct(Product product);

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse managerProductDetail(Integer prodcutId);

    ServerResponse<ProductDetailVO> getProductDetail(Integer prodcutId);

    ServerResponse<PageInfo> getProductListByKeyWordCatory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
