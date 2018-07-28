package com.eshop.controller.portal;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.enums.CartCheckEnum;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.User;
import com.eshop.service.impl.CartService;
import com.eshop.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by windvalley on 2018/7/26.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private CartService cartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVO> add(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        return cartService.add(user.getId(), productId, count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVO> update(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        return cartService.update(user.getId(), productId, count);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse<CartVO> delete(HttpSession session, String productId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        return cartService.delete(user.getId(), productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVO> list(HttpSession session){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        return cartService.list(user.getId());
    }

    @RequestMapping("checkStatusSelect.do")
    @ResponseBody
    public ServerResponse<CartVO> checkStatusSelect(HttpSession session, String productId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        return cartService.checkStatus(user.getId(), CartCheckEnum.CHECK.getCode(), productId);
    }

    @RequestMapping("unCheckStatusSelect.do")
    @ResponseBody
    public ServerResponse<CartVO> unCheckStatusSelect(HttpSession session, String productId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        return cartService.checkStatus(user.getId(), CartCheckEnum.UNCHECK.getCode(), productId);
    }

    @RequestMapping("checkStatusAll.do")
    @ResponseBody
    public ServerResponse<CartVO> checkStatusSelect(HttpSession session){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        return cartService.checkStatus(user.getId(), CartCheckEnum.CHECK.getCode(), null);
    }

    @RequestMapping("unCheckStatusAll.do")
    @ResponseBody
    public ServerResponse<CartVO> unCheckStatusSelect(HttpSession session){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        return cartService.checkStatus(user.getId(), CartCheckEnum.UNCHECK.getCode(), null);
    }

    @RequestMapping("getCartProductCount.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        }

        return cartService.getCartProductCount(user.getId());
    }
}