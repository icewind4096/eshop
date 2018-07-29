package com.eshop.service.impl;

import com.eshop.common.ServerResponse;
import com.eshop.dao.ShippingMapper;
import com.eshop.pojo.Shipping;
import com.eshop.service.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by windvalley on 2018/7/29.
 */
@Service
public class ShippingService implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        if (shippingMapper.insert(shipping) > 0){
            Map map = new HashMap();
            map.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功", map);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    @Override
    public ServerResponse delete(Integer userId, Integer shippingId) {
        if (shippingMapper.deleteByUserIdAndShippingId(userId, shippingId) > 0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        if (shippingMapper.updateByShipping(shipping) > 0){
            return ServerResponse.createBySuccessMessage("修改地址成功");
        }
        return ServerResponse.createByErrorMessage("修改地址失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId, shippingId);
        if (shipping != null){
            return ServerResponse.createBySuccessMessage("查询地址成功");
        }
        return ServerResponse.createByErrorMessage("查询线地址不存在");
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);

        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
