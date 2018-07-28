package com.eshop.service.impl;

import com.eshop.common.ServerResponse;
import com.eshop.dao.CartMapper;
import com.eshop.dao.ProductMapper;
import com.eshop.enums.CartCheckEnum;
import com.eshop.enums.ResponseEnum;
import com.eshop.pojo.Cart;
import com.eshop.pojo.Product;
import com.eshop.service.ICartService;
import com.eshop.util.BigDecimalUtil;
import com.eshop.util.PropertiesUtil;
import com.eshop.vo.CartProductVO;
import com.eshop.vo.CartVO;
import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by windvalley on 2018/7/26.
 */
@Service
public class CartService implements ICartService {
    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    public ServerResponse<CartVO> list(Integer userId) {
        if (userId == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.PARAMATERERR.getCode(), ResponseEnum.PARAMATERERR.getMessage());
        }

        CartVO cartVO = getCartVOLimit(userId);

        return ServerResponse.createBySuccess(cartVO);
    }

    @Override
    public ServerResponse<CartVO> checkStatus(Integer userId, Integer status, String productIds) {
        if (userId == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.PARAMATERERR.getCode(), ResponseEnum.PARAMATERERR.getMessage());
        }

        List<String> productIdList = null;
        if (productIds != null){
            productIdList = Splitter.on(",").splitToList(productIds);
            if (CollectionUtils.isEmpty(productIdList) == true){
                return ServerResponse.createByErrorMessage(ResponseEnum.PARAMATERERR.getCode(), ResponseEnum.PARAMATERERR.getMessage());
            }
        }

        cartMapper.checkStatusCartByUserIdAndProducts(userId, status, productIdList);

        CartVO cartVO = getCartVOLimit(userId);

        return ServerResponse.createBySuccess(cartVO);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId == null){
            return ServerResponse.createByErrorMessage(ResponseEnum.PARAMATERERR.getCode(), ResponseEnum.PARAMATERERR.getMessage());
        }

        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    @Override
    public ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count) {
        if ((userId == null) || count == null ){
            return ServerResponse.createByErrorMessage(ResponseEnum.PARAMATERERR.getCode(), ResponseEnum.PARAMATERERR.getMessage());
        }

        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        if (cart == null){
            cart = new Cart();
            cart.setQuantity(count);
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setChecked(CartCheckEnum.CHECK.getCode());
            cartMapper.insert(cart);
        }
        else {
            cart.setQuantity(cart.getQuantity() + count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        CartVO cartVO = getCartVOLimit(userId);

        return ServerResponse.createBySuccess(cartVO);
    }

    @Override
    public ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count) {
        if ((userId == null) || count == null ){
            return ServerResponse.createByErrorMessage(ResponseEnum.PARAMATERERR.getCode(), ResponseEnum.PARAMATERERR.getMessage());
        }
        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);

        if (cart != null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        CartVO cartVO = getCartVOLimit(userId);

        return ServerResponse.createBySuccess(cartVO);
    }

    @Override
    public ServerResponse<CartVO> delete(Integer userId, String productIds) {
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if ((userId == null) || (CollectionUtils.isEmpty(productIdList))){
            return ServerResponse.createByErrorMessage(ResponseEnum.PARAMATERERR.getCode(), ResponseEnum.PARAMATERERR.getMessage());
        }

        cartMapper.deleteCartByUserIdAndProducts(userId, productIdList);

        CartVO cartVO = getCartVOLimit(userId);

        return ServerResponse.createBySuccess(cartVO);
    }

    private CartVO getCartVOLimit(Integer userId){
        CartVO cartVO = new CartVO();

        List<Cart> cartList = cartMapper.selectCartByUserId(userId);

        List<CartProductVO> cartProductVOList = new ArrayList<CartProductVO>();

        if (CollectionUtils.isNotEmpty(cartList) == true){
            cartListTocartProductVOList(cartList, cartProductVOList);
            cartVO.setCartTotalPrice(calculateCartTotalPrice(cartProductVOList));
            cartVO.setCartProductVOList(cartProductVOList);
            cartVO.setAllChecked(calculateCartCheckAll(cartProductVOList));
            cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }

        return cartVO;
    }

    private Boolean calculateCartCheckAll(List<CartProductVO> cartList) {
        for (CartProductVO cartItem : cartList ) {
            if (cartItem.getProductChecked() == 0){
                return false;
            }
        }
        return true;
    }

    private BigDecimal calculateCartTotalPrice(List<CartProductVO> cartList) {
        BigDecimal totalPrice = new BigDecimal("0");
        for (CartProductVO cartItem : cartList ){
            if (cartItem.getProductChecked() == CartCheckEnum.CHECK.getCode()){
                BigDecimalUtil.add(totalPrice.floatValue(), cartItem.getProductTotalPrice().floatValue());
            }
        }
        return totalPrice;
    }

    private void cartListTocartProductVOList(List<Cart> cartList, List<CartProductVO> cartProductVOList) {
        for (Cart cartItem : cartList ){
            CartProductVO cartProductVO = assembleCartVO(cartItem);
            cartProductVOList.add(cartProductVO);
        }
    }

    private CartProductVO assembleCartVO(Cart cart) {
        CartProductVO cartProductVO = new CartProductVO();
        cartProductVO.setId(cart.getId());
        cartProductVO.setUserId(cart.getUserId());
        cartProductVO.setProductId(cart.getProductId());
        cartProductVO.setProductChecked(cart.getChecked());

        Product product = productMapper.selectByPrimaryKey(cart.getProductId());
        if (product != null){
            cartProductVO.setProductMainImage(product.getMainImage());
            cartProductVO.setProductName(product.getName());
            cartProductVO.setProductSubTitle(product.getSubtitle());
            cartProductVO.setProductStatus(product.getStatus());
            cartProductVO.setProductPrice(product.getPrice());
            cartProductVO.setProductStock(product.getStock());
            if (product.getStock() >= cart.getQuantity()){
                cartProductVO.setQuantity(cart.getQuantity());
                cartProductVO.setLimitQuantity(CartCheckEnum.LIMITNUMSUCCESS.getMessage());
            }
            else {
                cartProductVO.setQuantity(product.getStock());
                cartProductVO.setLimitQuantity(CartCheckEnum.LIMITNUMFAIL.getMessage());
                Cart cartForQuantity = new Cart();
                cartForQuantity.setQuantity(product.getStock());
                cartForQuantity.setId(cart.getId());
                cartMapper.updateByPrimaryKeySelective(cartForQuantity);
            }
            cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(cartProductVO.getProductPrice().doubleValue(), cartProductVO.getQuantity()));
        }

        return cartProductVO;
    }
}
