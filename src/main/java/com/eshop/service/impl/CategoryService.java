package com.eshop.service.impl;

import com.eshop.common.ServerResponse;
import com.eshop.dao.CategoryMapper;
import com.eshop.pojo.Category;
import com.eshop.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by mecwa on 2018/7/16.
 */
@Service
@Slf4j
public class CategoryService implements ICategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if ( (parentId == null) || (StringUtils.isBlank(categoryName) == true) ){
            return ServerResponse.createByErrorMessage("添加品名参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        if (categoryMapper.insert(category) > 0){
            return ServerResponse.createBySuccessMessage("添加品名成功");
        }

        return ServerResponse.createByErrorMessage("添加品名失败");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if ( (categoryId == null) || (StringUtils.isBlank(categoryName) == true) ){
            return ServerResponse.createByErrorMessage("修改品名参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        if (categoryMapper.updateByPrimaryKeySelective(category) > 0){
            return ServerResponse.createBySuccessMessage("修改品名成功");
        }
        return ServerResponse.createByErrorMessage("修改品名失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(int categoryId) {
        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categories) == true){
            log.info("未找到当前分类的子节点");
        }
        return ServerResponse.createBySuccess(categories);
    }

    //递归查询节点
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(int categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildrenCategory(categorySet, categoryId);

        List<Integer> categoryIds = Lists.newArrayList();
        if (categorySet != null){
            for (Category category: categorySet){
                categoryIds.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIds);
    }

    //用set可以天然排除重复记录，但是要复写HasCode和Equre方法
    private Set<Category> findChildrenCategory(Set<Category> categorySet, int categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem: categories){
            findChildrenCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}
