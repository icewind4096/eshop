package com.eshop.dao;

import com.eshop.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName, @Param("categoryIdList") List<Integer> categoryList);

    List<Product> selectList();

    List<Product> selectByNameAndId(@Param("productName")String productName, @Param("productId") Integer productId);
}