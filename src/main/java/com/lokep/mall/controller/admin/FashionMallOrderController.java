package com.lokep.mall.controller.admin;

import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.controller.vo.FashionMallOrderItemVO;
import com.lokep.mall.entity.FashionMallOrder;
import com.lokep.mall.service.FashionMallOrderService;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.Result;
import com.lokep.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class FashionMallOrderController {

    @Autowired
    private FashionMallOrderService orderService;

    @GetMapping("/orders")
    public String ordersPage(HttpServletRequest request){
        request.setAttribute("path","orders");
        return "admin/fashion_mall_order";
    }

    //列表
    @RequestMapping(value = "/orders/list",method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String ,Object> params){
        if (StringUtils.isEmpty(params.get("page"))||StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(orderService.getFasionMallOrdersPage(pageUtil));
    }

    /**
     * 修改订单
     * @param order
     * @return
     */
    @RequestMapping(value = "/orders/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody FashionMallOrder order){
        if (Objects.isNull(order.getTotalPrice())
                ||Objects.isNull(order.getOrderId())
                ||order.getOrderId() < 1
                ||order.getTotalPrice() < 1
                ||StringUtils.isEmpty(order.getUserAddress())){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        String result = orderService.updateOrder(order);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 配货
     * @param ids
     * @return
     */
    @RequestMapping(value = "/orders/checkDone",method = RequestMethod.POST)
    @ResponseBody
    public Result checkDone(@RequestBody Long[] ids){
        if (ids.length < 1){
            ResultGenerator.genFailResult("参数异常！！！");
        }
        String result = orderService.checkDone(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 出库
     * @param ids
     * @return
     */
    @RequestMapping(value = "/orders/checkOut", method = RequestMethod.POST)
    @ResponseBody
    public Result checkOut(@RequestBody Long[] ids){
        if (ids.length < 1){
            return ResultGenerator.genFailResult("参数异常！！！");
        }
        String result = orderService.checkOut(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 关闭订单
     * @param ids
     * @return
     */
    @RequestMapping(value = "/orders/close", method = RequestMethod.POST)
    @ResponseBody
    public Result closeOrder(@RequestBody Long[] ids){
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！！");
        }
        String result = orderService.closeOrder(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/order-items/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        List<FashionMallOrderItemVO> orderItems = orderService.getOrderItems(id);
        if (!CollectionUtils.isEmpty(orderItems)) {
            return ResultGenerator.genSuccessResult(orderItems);
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }


}
