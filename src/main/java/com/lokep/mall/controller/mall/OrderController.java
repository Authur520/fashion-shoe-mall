package com.lokep.mall.controller.mall;

import com.lokep.mall.common.Constants;
import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.controller.vo.FashionMallOrderDetailVO;
import com.lokep.mall.controller.vo.FashionMallShoppingCartItemVO;
import com.lokep.mall.controller.vo.FashionMallUserVO;
import com.lokep.mall.entity.FashionMallOrder;
import com.lokep.mall.service.FashionMallOrderService;
import com.lokep.mall.service.FashionMallShoppingCartService;

import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.Result;
import com.lokep.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    private FashionMallShoppingCartService shoppingCartService;
    @Autowired
    private FashionMallOrderService orderService;

    //TODO 去支付页面功能有问题
    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        FashionMallOrderDetailVO orderDetailVO = orderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailVO == null) {
            return "您访问的页面有问题！！！";
        }
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        System.out.println(user);
        params.put("userId", user.getUserId());
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", orderService.getMyOrders(pageUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }

    //提交订单
    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<FashionMallShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        System.out.println(myShoppingCartItems);
        //TODO 判断收货地址以及myShoppingCartItems
        //保存订单并返回订单号
        String saveOrderResult = orderService.saveOrder(user, myShoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/" + saveOrderResult;
    }

    // @GetMapping("/saveOrder")
    // public String saveOrder(HttpSession httpSession) {
    //     FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
    //     List<FashionMallShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
    //     if (StringUtils.isEmpty(user.getAddress().trim())) {
    //         //无收货地址
    //         return ServiceResultEnum.NULL_ADDRESS_ERROR.getResult();
    //     }
    //     if (CollectionUtils.isEmpty(myShoppingCartItems)) {
    //         //购物车中无数据则跳转至错误页
    //         return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    //     }
    //     //保存订单并返回订单号
    //     String saveOrderResult = orderService.saveOrder(user, myShoppingCartItems);
    //     //跳转到订单详情页
    //     return "redirect:/orders/" + saveOrderResult;
    // }


    //取消订单
    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = orderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    //完成订单
    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = orderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    //查看支付类型
    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        FashionMallOrder newBeeMallOrder = orderService.getFashionMallOrderByOrderNo(orderNo);
        // 判断订单userId
        // 判断订单状态
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", newBeeMallOrder.getTotalPrice());
        return "mall/pay-select";
    }

    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        FashionMallOrder newBeeMallOrder = orderService.getFashionMallOrderByOrderNo(orderNo);
        //判断订单userId
        //判断订单状态
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", newBeeMallOrder.getTotalPrice());
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }

    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = orderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

}
