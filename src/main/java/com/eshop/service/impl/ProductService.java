package com.eshop.service.impl;

import com.eshop.common.ServerResponse;
import com.eshop.dao.ProductMapper;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.Product;
import com.eshop.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by windvalley on 2018/7/18.
 */
@Service
public class ProductService implements IProductService {
    @Autowired
    ProductMapper productMapper;

    @Override
    public ServerResponse saveProduct(Product product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }

            if (product.getId() != null) {
                if (productMapper.updateByPrimaryKey(product) > 0) {
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("产品不存在");
            } else {
                if (productMapper.insert(product) > 0) {
                    return ServerResponse.createBySuccess("新建产品成功");
                }
                return ServerResponse.createByErrorMessage("新建产品不失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if ((productId == null) || (status == null)){
            return ServerResponse.createByErrorMessage(ResponseEnum.PARAMATERERR.getCode(), ResponseEnum.PARAMATERERR.getMessage());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        if (productMapper.updateByPrimaryKeySelective(product) > 0){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }
}
