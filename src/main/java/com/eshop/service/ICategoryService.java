package com.eshop.service;

import com.eshop.common.ServerResponse;
import com.eshop.pojo.Category;

import java.util.List;

/**
 * Created by mecwa on 2018/7/16.
 */
public interface ICategoryService {
    public ServerResponse addCategory(String categoryName, Integer parentId);

    public ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    public ServerResponse<List<Category>> getChildrenParallelCategory(int parentId);

    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(int categoryId);
}
