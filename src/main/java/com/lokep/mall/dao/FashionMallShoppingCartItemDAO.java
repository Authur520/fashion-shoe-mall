package com.lokep.mall.dao;

import com.lokep.mall.entity.FashionMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FashionMallShoppingCartItemDAO {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(FashionMallShoppingCartItem record);

    int insertSelective(FashionMallShoppingCartItem record);

    FashionMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    int updateByPrimaryKeySelective(FashionMallShoppingCartItem record);

    int updateByPrimaryKey(FashionMallShoppingCartItem record);

    List<FashionMallShoppingCartItem> selectByUserId(@Param("userId") Long userId, @Param("number") int number);

    int deleteBatch(List<Long> ids);

    FashionMallShoppingCartItem selectByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    int selectCountByUserId(Long userId);
}