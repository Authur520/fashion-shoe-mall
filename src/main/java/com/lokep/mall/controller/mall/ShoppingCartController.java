package com.lokep.mall.controller.mall;

import com.lokep.mall.common.Constants;
import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.controller.vo.FashionMallShoppingCartItemVO;
import com.lokep.mall.controller.vo.FashionMallUserVO;
import com.lokep.mall.entity.FashionMallShoppingCartItem;
import com.lokep.mall.service.FashionMallShoppingCartService;
import com.lokep.mall.util.Result;
import com.lokep.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Autowired
    private FashionMallShoppingCartService shoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request, HttpSession httpSession) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<FashionMallShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //订单项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(FashionMallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "您访问的页面出错了！！！";
            }
            //总价
            for (FashionMallShoppingCartItemVO fashionMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += fashionMallShoppingCartItemVO.getGoodsCount() * fashionMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "您访问的页面出错了！！！";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveFashionMallShoppingCartItem(@RequestBody FashionMallShoppingCartItem fashionMallShoppingCartItem,
                                                 HttpSession httpSession) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        System.out.println(user);
        fashionMallShoppingCartItem.setUserId(user.getUserId());
        //判断数量
        String saveResult = shoppingCartService.saveFashionMallCartItem(fashionMallShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateFashionMallShoppingCartItem(@RequestBody FashionMallShoppingCartItem fashionMallShoppingCartItem,
                                                   HttpSession httpSession) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        fashionMallShoppingCartItem.setUserId(user.getUserId());
        //判断数量
        String saveResult = shoppingCartService.updateFashionMallCartItem(fashionMallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(saveResult);
    }


    @DeleteMapping("/shop-cart/{fashionMallShoppingCartItemId}")
    @ResponseBody
    public Result updateFashionMallShoppingCartItem(@PathVariable("fashionMallShoppingCartItemId") Long fashionMallShoppingCartItemId,
                                                   HttpSession httpSession) {
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = shoppingCartService.deleteById(fashionMallShoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request, HttpSession httpSession) {
        int priceTotal = 0;
        FashionMallUserVO user = (FashionMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<FashionMallShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (FashionMallShoppingCartItemVO fashionMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += fashionMallShoppingCartItemVO.getGoodsCount() * fashionMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "您访问的页面出错了！！！";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }

}
