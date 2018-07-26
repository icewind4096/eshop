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

    ServerResponse managerProductDetail(Integer productId);

    ServerResponse<ProductDetailVO> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductListByKeyWordCatory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);

    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize, String orderBy);

    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize, String orderBy);
}
