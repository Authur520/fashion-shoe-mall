package com.lokep.mall.service;

import com.lokep.mall.controller.vo.FashionMallShoppingCartItemVO;
import com.lokep.mall.entity.FashionMallShoppingCartItem;

import java.util.List;

public interface FashionMallShoppingCartService {

    /**
     *获取我的购物车中的列表数据
     * @param userId
     * @return
     */
    List<FashionMallShoppingCartItemVO> getMyShoppingCartItems(Long userId);

    /**
     * 保存商品至购物车
     * @param fashionMallShoppingCartItem
     * @return
     */
    String saveFashionMallCartItem(FashionMallShoppingCartItem fashionMallShoppingCartItem);

    /**
     * 修改购物车中的属性
     * @param fashionMallShoppingCartItem
     * @return
     */
    String updateFashionMallCartItem(FashionMallShoppingCartItem fashionMallShoppingCartItem);

    /**
     * 删除购物车中的商品
     * @param fashionMallShoppingCartItemId
     * @return
     */
    Boolean deleteById(Long fashionMallShoppingCartItemId);

}
