package com.eshop.service.impl;

import com.eshop.common.ServerResponse;
import com.eshop.dao.CategoryMapper;
import com.eshop.dao.ProductMapper;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.Category;
import com.eshop.pojo.Product;
import com.eshop.service.IProductService;
import com.eshop.util.DateTimeUtil;
import com.eshop.util.PropertiesUtil;
import com.eshop.vo.ProductDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by windvalley on 2018/7/18.
 */
@Service
public class ProductService implements IProductService {
    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;

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

    @Override
    public ServerResponse<ProductDetailVO> managerProductDetail(Integer prodcutId) {
        if (prodcutId == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.PARAMATERERR.getCode(), ResponseEnum.PARAMATERERR.getMessage());
        }

        Product product = productMapper.selectByPrimaryKey(prodcutId);
        if (product == null)  {
            return ServerResponse.createByErrorMessage("产品已删除");
        }

        if (product.getStatus() == 0)  {
            return ServerResponse.createByErrorMessage("产品已下架");
        }

        return ServerResponse.createBySuccess(assembleProductDetailVO(product));
    }

    private ProductDetailVO assembleProductDetailVO(Product product) {
        ProductDetailVO productDetailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product, productDetailVO);
        productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.eshop.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
            productDetailVO.setParentCategoryId(0);
        } else {
            productDetailVO.setParentCategoryId(category.getParentId());
        }

        productDetailVO.setCreateTime(DateTimeUtil.dateTostring(product.getCreateTime()));
        productDetailVO.setUpdateTime(DateTimeUtil.dateTostring(product.getUpdateTime()));

        return productDetailVO;
    }
}
