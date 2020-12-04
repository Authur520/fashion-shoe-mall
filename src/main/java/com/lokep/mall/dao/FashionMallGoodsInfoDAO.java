package com.lokep.mall.dao;

import com.lokep.mall.entity.FashionMallGoodsInfo;
import com.lokep.mall.entity.StockNumDTO;
import com.lokep.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FashionMallGoodsInfoDAO {
    int deleteByPrimaryKey(Long goodsId);

    int insert(FashionMallGoodsInfo record);

    int insertSelective(FashionMallGoodsInfo record);

    FashionMallGoodsInfo selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(FashionMallGoodsInfo record);

    int updateByPrimaryKeyWithBLOBs(FashionMallGoodsInfo record);

    int updateByPrimaryKey(FashionMallGoodsInfo record);

    List<FashionMallGoodsInfo> selectByPrimaryKey(List<Long> goodsIds);

    List<FashionMallGoodsInfo> findFashionMallGoodsList(PageQueryUtil pageUtil);

    int getTotalFashionMallGoods(PageQueryUtil pageUtil);

    //TODO 商品的上下架
    int batchUpdateSellStatus(@Param("goodsId") Long[] ids, @Param("sellStatus") int sellStatus);

    List<FashionMallGoodsInfo> findNewBeeMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalNewBeeMallGoodsBySearch(PageQueryUtil pageUtil);

    List<FashionMallGoodsInfo> selectByPrimaryKeys(List<Long> goodsIds);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);
}