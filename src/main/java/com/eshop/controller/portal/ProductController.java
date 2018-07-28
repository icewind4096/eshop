package com.eshop.controller.portal;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.User;
import com.eshop.service.impl.ProductService;
import com.eshop.vo.ProductDetailVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by windvalley on 2018/7/22.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    ProductService productService;

    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVO> getDetail(HttpSession session, Integer productId){
        return productService.getProductDetail(productId);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> getList(@RequestParam(value = "keyword", required = false)String keyword, @RequestParam(value = "categoryId", required = false)Integer categoryId
                                        ,@RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize
                                        ,@RequestParam(value = "orderBy", defaultValue = "")String orderBy){
        return productService.getProductListByKeyWordCatory(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
