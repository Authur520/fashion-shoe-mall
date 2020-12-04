package com.lokep.mall.dao;

import com.lokep.mall.entity.FashionMallOrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FashionMallOrderItemDAO {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(FashionMallOrderItem record);

    int insertSelective(FashionMallOrderItem record);

    FashionMallOrderItem selectByPrimaryKey(Long orderItemId);

    int updateByPrimaryKeySelective(FashionMallOrderItem record);

    int updateByPrimaryKey(FashionMallOrderItem record);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<FashionMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<FashionMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<FashionMallOrderItem> orderItems);

}