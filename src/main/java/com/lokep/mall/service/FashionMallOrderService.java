package com.lokep.mall.service;

import com.lokep.mall.controller.vo.FashionMallOrderDetailVO;
import com.lokep.mall.controller.vo.FashionMallOrderItemVO;
import com.lokep.mall.controller.vo.FashionMallShoppingCartItemVO;
import com.lokep.mall.controller.vo.FashionMallUserVO;
import com.lokep.mall.entity.FashionMallOrder;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;

import java.util.List;

public interface FashionMallOrderService {

    /**
     * 后台分页
     * @param pageUtil
     * @return
     */
    PageResult getFasionMallOrdersPage(PageQueryUtil pageUtil);

    /**
     * 修改订单信息
     * @param order
     * @return
     */
    String updateOrder(FashionMallOrder order);

    /**
     * 订单配货
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 订单出库
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    /**
     * 获取订单详情
     * @param orderNo
     * @param userId
     * @return
     */
    FashionMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 提交订单
     * @param user
     * @param myShoppingCartItems
     * @return
     */
    String saveOrder(FashionMallUserVO user, List<FashionMallShoppingCartItemVO> myShoppingCartItems);
    //String saveOrder(FashionMallUserVO user, List<FashionMallShoppingCartItemVO> myShoppingCartItems);
    /**
     * 手动取消订单
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    /**
     * 获取订单详情
     * @param orderNo
     * @return
     */
    FashionMallOrder getFashionMallOrderByOrderNo(String orderNo);

    /**
     *支付成功
     * @param orderNo
     * @param payType
     * @return
     */
    String paySuccess(String orderNo, int payType);

    List<FashionMallOrderItemVO> getOrderItems(Long id);
}
