package com.eshop.controller.backend;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.controller.common.SecurityUtil;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.User;
import com.eshop.service.impl.OrderService;
import com.eshop.service.impl.UserService;
import com.eshop.vo.OrderVO;
import com.github.pagehelper.PageInfo;
import com.mysql.fabric.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by windvalley on 2018/8/11.
 */
@Controller
@RequestMapping("/manager/order")
public class OrderManagerController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpServletRequest httpServletRequest
            ,@RequestParam(value = "pageNumb", defaultValue = "1") int pageNum
            ,@RequestParam(value = "pageSize", defaultValue = "1") int pageSize) {
        User user = SecurityUtil.getUserInfoByLoginToken(httpServletRequest);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        if (userService.checkAdminRole(user).isSuccess() == false) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户没有管理员权限");
        }

        return orderService.managerList(pageNum, pageSize);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVO> detail(HttpServletRequest httpServletRequest, Long orderNo) {
        User user = SecurityUtil.getUserInfoByLoginToken(httpServletRequest);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        if (userService.checkAdminRole(user).isSuccess() == false) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户没有管理员权限");
        }

        return orderService.managerDetail(orderNo);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpServletRequest httpServletRequest, Long orderNo
            ,@RequestParam(value = "pageNumb", defaultValue = "1") int pageNum
            ,@RequestParam(value = "pageSize", defaultValue = "1") int pageSize) {
        User user = SecurityUtil.getUserInfoByLoginToken(httpServletRequest);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        if (userService.checkAdminRole(user).isSuccess() == false) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户没有管理员权限");
        }

        return orderService.managerSearch(orderNo, pageNum, pageSize);
    }

    @RequestMapping("sendGoods.do")
    @ResponseBody
    public ServerResponse<String> sendGoods(HttpServletRequest httpServletRequest, Long orderNo) {
        User user = SecurityUtil.getUserInfoByLoginToken(httpServletRequest);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        if (userService.checkAdminRole(user).isSuccess() == false) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户没有管理员权限");
        }

        return orderService.managerSendGoods(orderNo);
    }
}
