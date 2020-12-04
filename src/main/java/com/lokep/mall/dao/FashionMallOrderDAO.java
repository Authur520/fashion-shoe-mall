package com.lokep.mall.dao;

import com.lokep.mall.entity.FashionMallOrder;
import com.lokep.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FashionMallOrderDAO {
    int deleteByPrimaryKey(Long orderId);

    int insert(FashionMallOrder record);

    int insertSelective(FashionMallOrder record);

    FashionMallOrder selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(FashionMallOrder record);

    int updateByPrimaryKey(FashionMallOrder record);

    List<FashionMallOrder> findFashionMallOrderlist(PageQueryUtil pageUtil);

    int getTotalFashionMallOrder(PageQueryUtil pageUtil);

    List<FashionMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkDone(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds")List<Long> orderIds, @Param("orderStatus") int orderStatus);

    FashionMallOrder selectByOrderNo(String orderNo);

    int getTotalFashionMallOrders(PageQueryUtil pageUtil);

    List<FashionMallOrder> findFashionMallOrderList(PageQueryUtil pageUtil);
}