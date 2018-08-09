package com.eshop.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.enums.PayResponseEnum;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.User;
import com.eshop.service.impl.OrderService;
import com.eshop.vo.OrderProductVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by windvalley on 2018/8/4.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService orderService;

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        return orderService.pay(orderNo, user.getId(), path);
    }

    @RequestMapping("alipaycallback.do")
    @ResponseBody
    public Object alipayCallBack(HttpServletRequest request){
        Map<String, String> params = new HashMap<String, String>();
        Map requestParamaters = request.getParameterMap();
        for (Iterator iterator = requestParamaters.keySet().iterator(); iterator.hasNext();){
            String name = (String) iterator.next();
            String[] values = (String[]) requestParamaters.get(name);
            String value = "";
            for (int i = 0; i < values.length; i ++){
                value = (i == values.length - 1) ? value + values[i]: value + values[i] + ",";
            }
            params.put(name, value);
        }

        logger.info("支付宝回调, sign:{}, tradeStatus:{}, 参数:{}", params.get("sign"), params.get("trade_status"), params.toString());

        //非常重要，验证回调的正取性，是否支付宝调用，同时避免重复通知
        //支付宝SDK说要移除sign和sign_Type两项
        params.remove("sign_type");
        try {
            boolean alipayRSACheckv2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());

            if (alipayRSACheckv2 == false){
                return ServerResponse.createByErrorMessage("非法请求");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证异常", e);
        }

        ServerResponse serverResponse = orderService.alipayCallBack(params);
        if (serverResponse.isSuccess() == true){
            return "success";
        }
        return "failed";
    }

    @RequestMapping("queryOrderPayStatus.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }

        if (orderService.queryOrderPayStatus(user.getId(), orderNo).isSuccess() == true){
            return ServerResponse.createBySuccess(true);
        }
        return  ServerResponse.createBySuccess(false);
    }

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }
        return orderService.create(user.getId(), shippingId);
    }

    @RequestMapping("getOrderCateProduct.do")
    @ResponseBody
    public ServerResponse getOrderCateProduct(HttpSession session) {
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), ResponseEnum.NEEDLOGIN.getMessage());
        }
        return orderService.getOrderCateProduct(user.getId());
    }
}
