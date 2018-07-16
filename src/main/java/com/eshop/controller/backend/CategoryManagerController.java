package com.eshop.controller.backend;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.User;
import com.eshop.service.ICategoryService;
import com.eshop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by mecwa on 2018/7/16.
 */
@Controller
@RequestMapping("/manager/category")
public class CategoryManagerController {
    @Autowired
    IUserService userService;

    @Autowired
    ICategoryService categoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
           return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户们管理员权限");
        }

        return categoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse updateCategoryName(HttpSession session, Integer categoryId, String categoryName){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户们管理员权限");
        }

        return categoryService.updateCategoryName(categoryId, categoryName);
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") int categoryId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户们管理员权限");
        }

        return categoryService.getChildrenParallelCategory(categoryId);
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getDeepChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") int categoryId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户们管理员权限");
        }

        return categoryService.selectCategoryAndChildrenById(categoryId);
    }
}
