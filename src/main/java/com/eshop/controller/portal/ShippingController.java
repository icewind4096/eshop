package com.eshop.controller.portal;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.Shipping;
import com.eshop.pojo.User;
import com.eshop.service.impl.ShippingService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by windvalley on 2018/7/29.
 */
@Controller
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private ShippingService shippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }
        return shippingService.add(user.getId(), shipping);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }
        return shippingService.delete(user.getId(), shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }
        return shippingService.update(user.getId(), shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }
        return shippingService.select(user.getId(), shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum
                                    ,@RequestParam(value = "pageSize", defaultValue = "1") int pageSize){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }
        return shippingService.list(user.getId(), pageNum, pageSize);
    }
}
