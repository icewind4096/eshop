package com.eshop.controller.backend;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.Product;
import com.eshop.pojo.User;
import com.eshop.service.IProductService;
import com.eshop.service.IUserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by windvalley on 2018/7/18.
 */
@Controller
@RequestMapping("/manager/product")
public class ProductManagerController {
    @Autowired
    private IUserService userService;

    @Autowired
    private IProductService productService;

    /**
     * 保存/修改产品
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户无管理员权限");
        }

        return productService.saveProduct(product);
    }

    /**
     * 修改产品上下架
     * @param session
     * @param prodcutId
     * @param status
     * @return
     */
    @RequestMapping(value = "setSaleStatus.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer prodcutId, Integer status){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户无管理员权限");
        }

        return productService.setSaleStatus(prodcutId, status);
    }

    /**
     * 产品详情
     * @param session
     * @param prodcutId
     * @return
     */
    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer prodcutId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户无管理员权限");
        }

        return productService.managerProductDetail(prodcutId);
    }

    /**
     * 产品查询分页列表
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping(value = "list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession session
            , @RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize
            , @RequestParam(value = "orderBy", defaultValue = "")String orderBy){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户无管理员权限");
        }
        return productService.getProductList(pageNum, pageSize, orderBy);
    }
}
