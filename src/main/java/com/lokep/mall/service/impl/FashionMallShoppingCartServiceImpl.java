package com.lokep.mall.service.impl;

import com.lokep.mall.common.Constants;
import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.controller.vo.FashionMallShoppingCartItemVO;
import com.lokep.mall.dao.FashionMallGoodsInfoDAO;
import com.lokep.mall.dao.FashionMallShoppingCartItemDAO;
import com.lokep.mall.entity.FashionMallGoodsInfo;
import com.lokep.mall.entity.FashionMallShoppingCartItem;
import com.lokep.mall.service.FashionMallShoppingCartService;
import com.lokep.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FashionMallShoppingCartServiceImpl implements FashionMallShoppingCartService {

    @Autowired
    private FashionMallShoppingCartItemDAO shoppingCartItemDAO;

    @Autowired
    private FashionMallGoodsInfoDAO goodsInfoDAO;

    @Override
    public List<FashionMallShoppingCartItemVO> getMyShoppingCartItems(Long userId) {
        List<FashionMallShoppingCartItemVO> fashionMallShoppingCartItemVOS = new ArrayList<>();
        List<FashionMallShoppingCartItem> fashionMallShoppingCartItems = shoppingCartItemDAO.selectByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(fashionMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> fashionMallGoodsIds = fashionMallShoppingCartItems.stream().map(FashionMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            //TODO id
            List<FashionMallGoodsInfo> goodsInfos = goodsInfoDAO.selectByPrimaryKeys(fashionMallGoodsIds);
            Map<Long, FashionMallGoodsInfo> fashionMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(goodsInfos)) {
                fashionMallGoodsMap = goodsInfos.stream().collect(Collectors.toMap(FashionMallGoodsInfo::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (FashionMallShoppingCartItem fashionMallShoppingCartItem : fashionMallShoppingCartItems) {
                FashionMallShoppingCartItemVO fashionMallShoppingCartItemVO = new FashionMallShoppingCartItemVO();
                BeanUtil.copyProperties(fashionMallShoppingCartItem, fashionMallShoppingCartItemVO);
                if (fashionMallGoodsMap.containsKey(fashionMallShoppingCartItem.getGoodsId())) {
                    FashionMallGoodsInfo fashionMallGoodsTemp = fashionMallGoodsMap.get(fashionMallShoppingCartItem.getGoodsId());
                    fashionMallShoppingCartItemVO.setGoodsCoverImg(fashionMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = fashionMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    fashionMallShoppingCartItemVO.setGoodsName(goodsName);
                    fashionMallShoppingCartItemVO.setSellingPrice(fashionMallGoodsTemp.getSellingPrice());
                    fashionMallShoppingCartItemVOS.add(fashionMallShoppingCartItemVO);
                }
            }
        }
        return fashionMallShoppingCartItemVOS;
    }

    @Override
    public String saveFashionMallCartItem(FashionMallShoppingCartItem fashionMallShoppingCartItem) {
        FashionMallShoppingCartItem temp = shoppingCartItemDAO.selectByUserIdAndGoodsId(fashionMallShoppingCartItem.getUserId(), fashionMallShoppingCartItem.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            //count = tempCount + 1
            temp.setGoodsCount(fashionMallShoppingCartItem.getGoodsCount());
            return updateFashionMallCartItem(temp);
        }
        FashionMallGoodsInfo newBeeMallGoods = goodsInfoDAO.selectByPrimaryKey(fashionMallShoppingCartItem.getGoodsId());
        //商品为空
        if (newBeeMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = shoppingCartItemDAO.selectCountByUserId(fashionMallShoppingCartItem.getUserId());
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (shoppingCartItemDAO.insertSelective(fashionMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    public String updateFashionMallCartItem(FashionMallShoppingCartItem fashionMallShoppingCartItem) {
        FashionMallShoppingCartItem fashionMallShoppingCartItem1 = shoppingCartItemDAO.selectByPrimaryKey(fashionMallShoppingCartItem.getCartItemId());
        if (fashionMallShoppingCartItem1 == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出最大数量
        if (fashionMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        // 数量相同不会进行修改
        // userId不同不能修改
        fashionMallShoppingCartItem1.setGoodsCount(fashionMallShoppingCartItem.getGoodsCount());
        fashionMallShoppingCartItem1.setUpdateTime(new Date());
        //保存记录
        if (shoppingCartItemDAO.updateByPrimaryKeySelective(fashionMallShoppingCartItem1) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();

    }

    @Override
    public Boolean deleteById(Long fashionMallShoppingCartItemId) {
        //userId不同不能删除
        return shoppingCartItemDAO.deleteByPrimaryKey(fashionMallShoppingCartItemId) > 0;
    }


}
