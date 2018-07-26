package com.eshop.controller.backend;

import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.Product;
import com.eshop.pojo.User;
import com.eshop.service.IFileService;
import com.eshop.service.IProductService;
import com.eshop.service.IUserService;
import com.eshop.util.PropertiesUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

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

    @Autowired
    private IFileService fileService;

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
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户无管理员权限");
        }

        return productService.setSaleStatus(productId, status);
    }

    /**
     * 产品详情
     * @param session
     * @param prodcutId
     * @return
     */
    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户无管理员权限");
        }

        return productService.managerProductDetail(productId);
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

    @RequestMapping(value = "search.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpSession session
            ,@RequestParam(value = "productName", required = false)String productName, @RequestParam(value = "productId", required = false)Integer productId
            ,@RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize
            ,@RequestParam(value = "orderBy", defaultValue = "")String orderBy){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户无管理员权限");
        }
        return productService.searchProduct(productName, productId, pageNum, pageSize, orderBy);
    }

    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile, HttpServletRequest request){
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户未登录");
        }

        if (userService.checkAdminRole(user).isSuccess() == false){
            return ServerResponse.createByErrorMessage(ResponseEnum.NEEDLOGIN.getCode(), "用户无管理员权限");
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(uploadFile, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);

        return ServerResponse.createBySuccess(fileMap);
    }

    @RequestMapping(value = "richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richTextUpload(HttpSession session, @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(ConstVariable.CURRENTUSER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "用户未登录 ");
            return resultMap;
        }

        if (userService.checkAdminRole(user).isSuccess() == false) {
            resultMap.put("success", false);
            resultMap.put("msg", "无管理员权限");
            return resultMap;
        }

        //使用的是simditor，所以必须按照simditor的要求放回
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(uploadFile, path);
        if (StringUtils.isBlank(targetFileName) == true){
            resultMap.put("success", false);
            resultMap.put("msg", "上传失败");
            return resultMap;
        }

        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

        resultMap.put("success", true);
        resultMap.put("msg", "上传成功");
        resultMap.put("file_path", url);
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");

        return resultMap;
    }
}
