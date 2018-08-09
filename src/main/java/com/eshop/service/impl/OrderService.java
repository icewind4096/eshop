package com.eshop.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.eshop.common.ConstVariable;
import com.eshop.common.ServerResponse;
import com.eshop.dao.*;
import com.eshop.enums.OrderStatusEnum;
import com.eshop.enums.PayFlatformEnum;
import com.eshop.enums.PaymentTypeEnum;
import com.eshop.enums.ProductStatusEnum;
import com.eshop.pojo.*;
import com.eshop.service.IOrderService;
import com.eshop.util.BigDecimalUtil;
import com.eshop.util.DateTimeUtil;
import com.eshop.util.FTPUtil;
import com.eshop.util.PropertiesUtil;
import com.eshop.vo.OrderItemVO;
import com.eshop.vo.OrderProductVO;
import com.eshop.vo.OrderVO;
import com.eshop.vo.ShippingVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by windvalley on 2018/8/5.
 */
@Service
public class OrderService implements IOrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    PayInfoMapper payInfoMapper;

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        Map<String, String> map = new HashMap<String, String>();
        Order order = orderMapper.findByUserIdAndOrderNo(userId, orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("用户订单不存在");
        }

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(order.getOrderNo());

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append(ConstVariable.PAYTYPESCANER).append("订单号:" + outTradeNo.toString()).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = order.getPayment().toString();

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.getListByOrderNoAndUserId(orderNo, userId);
        for (OrderItem orderItem: orderItemList){
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString()
                                                        ,orderItem.getProductName()
                                                        ,BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue()
                                                        ,orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        // 支付宝当面付2.0服务
        AlipayTradeService tradeService;

        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if (folder.exists() == false){
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                logger.info("qrFilePath:" + qrPath);
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码文件异常" + qrPath);
                }
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();

                map.put("orderNo", String.valueOf(order.getOrderNo()));
                map.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(map);
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");
            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");
            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    @Override
    public ServerResponse alipayCallBack(Map<String, String> paramaters) {
        Long orderNo = Long.parseLong(paramaters.get("out_trade_no"));
        String tradeNo = paramaters.get("trade_no");
        String tradeStatus = paramaters.get("trade_status");

        Order order = orderMapper.findByOrderId(orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("非EShop订单");
        }

        if (order.getStatus() != OrderStatusEnum.NOPAY.getCode()){
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        }

        if (tradeStatus.equals("TRADE_SUCCESS") == true){
            order.setStatus(OrderStatusEnum.PAY.getCode());
            order.setPaymentTime(DateTimeUtil.stringToDate(paramaters.get("gmt_payment")));
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(PayFlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.findByUserIdAndOrderNo(userId, orderNo);

        if (order == null){
            return ServerResponse.createByErrorMessage("用户订单不存在");
        }

        if (order.getStatus() != OrderStatusEnum.NOPAY.getCode()){
            return ServerResponse.createBySuccess();
        }

        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse create(Integer userId, Integer shippingId) {
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment = getOrderTotalPrice(orderItemList);

        if (CollectionUtils.isEmpty(orderItemList) == true){
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        Order order = assembleOrder(userId, shippingId, payment);
        if (order == null){
            return ServerResponse.createByErrorMessage("产生用户订单错误");
        }

        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }

        orderItemMapper.insertOrderItemList(orderItemList);

        reduceProductStock(orderItemList);

        cleanCart(cartList);

        OrderVO orderVO = assembleOrderVO(order, orderItemList);
        return ServerResponse.createBySuccess(orderVO);
    }

    private OrderVO assembleOrderVO(Order order, List<OrderItem> orderItemList) {
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        orderVO.setPaymentTypeDesc(PaymentTypeEnum.codeOf(order.getPaymentType()).getMessage());
        orderVO.setPostage(order.getPostage());
        orderVO.setStatus(order.getStatus());
        orderVO.setStatusDesc(OrderStatusEnum.codeOf(order.getStatus()).getMessage());
        orderVO.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null){
            orderVO.setReceiverName(shipping.getReceiverName());
            orderVO.setShippingVO(assembleShippingVO(shipping));
        }

        orderVO.setPaymentTime(DateTimeUtil.dateTostring(order.getPaymentTime()));
        orderVO.setSendTime(DateTimeUtil.dateTostring(order.getSendTime()));
        orderVO.setCreateTime(DateTimeUtil.dateTostring(order.getCreateTime()));
        orderVO.setCloseTime(DateTimeUtil.dateTostring(order.getCloseTime()));

        orderVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVO> orderItemVOLists = new ArrayList<OrderItemVO>();
        for (OrderItem orderItem: orderItemList){
            orderItemVOLists.add(assembleOrderItemVO(orderItem));
        }
        orderVO.setOrderItemVOList(orderItemVOLists);

        return orderVO;
    }

    private OrderItemVO assembleOrderItemVO(OrderItem orderItem) {
        OrderItemVO orderItemVO = new OrderItemVO();
        BeanUtils.copyProperties(orderItem, orderItemVO);
        orderItemVO.setCreateTime(DateTimeUtil.dateTostring(orderItem.getCreateTime()));
        return orderItemVO;
    }

    private ShippingVO assembleShippingVO(Shipping shipping) {
        ShippingVO shippingVO = new ShippingVO();
        BeanUtils.copyProperties(shipping, shippingVO);
        return shippingVO;
    }

    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        long orderNo = generalOrderNo();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setStatus(OrderStatusEnum.NOPAY.getCode());
        order.setPostage(0);
        order.setPaymentType(PaymentTypeEnum.ONLINEPAY.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);

        if (orderMapper.insert(order) > 0){
            return order;
        }
        return null;
    }

    private  long generalOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal value = new BigDecimal("0");
        for (OrderItem orderItem: orderItemList){
            value = BigDecimalUtil.add(value.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return value;
    }

    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        if (CollectionUtils.isEmpty(cartList) == true){
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        for (Cart cart: cartList){
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product.getStatus() != ProductStatusEnum.ONSALE.getCode()){
                return ServerResponse.createByErrorMessage("产品已下架");
            }

            if (cart.getQuantity() >= product.getStock()){
                return ServerResponse.createByErrorMessage("产品 " + product.getName() + " 库存不足");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity()));
            orderItemList.add(orderItem);
        }

        return ServerResponse.createBySuccess(orderItemList);
    }

    @Override
    public ServerResponse<String> cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.findByUserIdAndOrderNo(userId, orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("该用户订单不存在");
        }

        if (order.getStatus() != OrderStatusEnum.NOPAY.getCode()){
            return ServerResponse.createByErrorMessage("该用户订单已付款,不可取消");
        }

        order.setStatus(OrderStatusEnum.CANCEL.getCode());
        if (orderMapper.updateByPrimaryKeySelective(order) > 0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<OrderProductVO> getOrderCateProduct(Integer userId) {
        OrderProductVO orderProductVO = new OrderProductVO();

        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        ServerResponse serverResponse = getCartOrderItem(userId, cartList);

        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();

        List<OrderItemVO> orderItemVOList = new ArrayList<OrderItemVO>();

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem: orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVOList.add(assembleOrderItemVO(orderItem));
        }
        orderProductVO.setProductTotalPrice(payment);
        orderProductVO.setOrderItemVOList(orderItemVOList);
        orderProductVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVO);
    }
}