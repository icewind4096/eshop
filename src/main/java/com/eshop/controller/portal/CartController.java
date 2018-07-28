package com.eshop.controller.portal;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.User;
import com.eshop.service.impl.CartService;
import com.eshop.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * Created by windvalley on 2018/7/26.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private CartService cartService;

    public ServerResponse<CartVO> add(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        return cartService.add(user.getId(), productId, count);
    }
}
