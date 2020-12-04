package com.lokep.mall.service.impl;

import com.lokep.mall.common.OrderStatusEnum;
import com.lokep.mall.common.PayStatusEnum;
import com.lokep.mall.common.PayTypeEnum;
import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.controller.vo.*;
import com.lokep.mall.dao.*;
import com.lokep.mall.entity.FashionMallGoodsInfo;
import com.lokep.mall.entity.FashionMallOrder;
import com.lokep.mall.entity.FashionMallOrderItem;
import com.lokep.mall.entity.StockNumDTO;
import com.lokep.mall.service.FashionMallOrderService;
import com.lokep.mall.util.BeanUtil;
import com.lokep.mall.util.NumberUtil;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;

@Service
public class FashionMallOrderServiceImpl implements FashionMallOrderService {

    @Autowired
    private FashionMallOrderDAO orderDAO;
    @Autowired
    private FashionMallOrderItemDAO orderItemDAO;
    @Autowired
    private FashionMallShoppingCartItemDAO shoppingCartItemDAO;
    @Autowired
    private FashionMallGoodsInfoDAO goodsInfoDAO;

    @Override
    public PageResult getFasionMallOrdersPage(PageQueryUtil pageUtil) {
        List<FashionMallOrder> ordersList = orderDAO.findFashionMallOrderlist(pageUtil);
        int total = orderDAO.getTotalFashionMallOrder(pageUtil);
        PageResult pageResult = new PageResult(ordersList, total, pageUtil.getLimit(),pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String updateOrder(FashionMallOrder order) {
        FashionMallOrder temp = orderDAO.selectByPrimaryKey(order.getOrderId());
        if (temp !=null && temp.getOrderStatus() >=0 && temp.getOrderStatus() < 3){
            temp.setTotalPrice(order.getTotalPrice());
            temp.setUserAddress(order.getUserAddress());
            temp.setUpdateTime(new Date());
            if (orderDAO.updateByPrimaryKeySelective(temp) > 0){
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<FashionMallOrder> orders = orderDAO.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (FashionMallOrder fashionMallOrder : orders) {
                if (fashionMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += fashionMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (fashionMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += fashionMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (orderDAO.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<FashionMallOrder> orders = orderDAO.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (FashionMallOrder fashionMallOrder : orders) {
                if (fashionMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += fashionMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (fashionMallOrder.getOrderStatus() != 1 && fashionMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += fashionMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (orderDAO.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<FashionMallOrder> orders = orderDAO.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (FashionMallOrder fashionMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (fashionMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += fashionMallOrder.getOrderNo() + ' ';
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (fashionMallOrder.getOrderStatus() == 4 || fashionMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += fashionMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (orderDAO.closeOrder(Arrays.asList(ids), OrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public FashionMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        FashionMallOrder fashionMallOrder = orderDAO.selectByOrderNo(orderNo);
        if (fashionMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            List<FashionMallOrderItem> orderItems = orderItemDAO.selectByOrderId(fashionMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<FashionMallOrderItemVO> fashionMallOrderItemVOS = BeanUtil.copyList(orderItems, FashionMallOrderItemVO.class);
                FashionMallOrderDetailVO fashionMallOrderDetailVO = new FashionMallOrderDetailVO();
                BeanUtil.copyProperties(fashionMallOrder, fashionMallOrderDetailVO);
                fashionMallOrderDetailVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(fashionMallOrderDetailVO.getOrderStatus()).getName());
                fashionMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(fashionMallOrderDetailVO.getPayType()).getName());
                fashionMallOrderDetailVO.setFashionMallOrderItemVOS(fashionMallOrderItemVOS);
                return fashionMallOrderDetailVO;
            }
        }
        return null;
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = orderDAO.getTotalFashionMallOrders(pageUtil);
        List<FashionMallOrder> fashionMallOrders = orderDAO.findFashionMallOrderList(pageUtil);
        List<FashionMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(fashionMallOrders, FashionMallOrderListVO.class);
            //设置订单状态中文显示值
            for (FashionMallOrderListVO fashionMallOrderListVO : orderListVOS) {
                fashionMallOrderListVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(fashionMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = fashionMallOrders.stream().map(FashionMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<FashionMallOrderItem> orderItems = orderItemDAO.selectByOrderIds(orderIds);
                Map<Long, List<FashionMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(FashionMallOrderItem::getOrderId));
                for (FashionMallOrderListVO fashionMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(fashionMallOrderListVO.getOrderId())) {
                        List<FashionMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(fashionMallOrderListVO.getOrderId());
                        //将NewBeeMallOrderItem对象列表转换成NewBeeMallOrderItemVO对象列表
                        List<FashionMallOrderItemVO> fashionMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, FashionMallOrderItemVO.class);
                        fashionMallOrderListVO.setFashionMallOrderItemVOS(fashionMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    // @Override
    // @Transactional
    // public String saveOrder(FashionMallUserVO user, List<FashionMallShoppingCartItemVO> myShoppingCartItems) {
    //     List<Long> itemIdList = myShoppingCartItems.stream().map(FashionMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
    //     List<Long> goodsIds = myShoppingCartItems.stream().map(FashionMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
    //     //TODO ddddd
    //     List<FashionMallGoodsInfo> newBeeMallGoods = goodsInfoDAO.selectByPrimaryKeys(goodsIds);
    //     Map<Long, FashionMallGoodsInfo> newBeeMallGoodsMap = newBeeMallGoods.stream().collect(Collectors.toMap(FashionMallGoodsInfo::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
    //     //判断商品库存
    //     for (FashionMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
    //         //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
    //         if (!newBeeMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
    //             return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    //         }
    //         //存在数量大于库存的情况，直接返回错误提醒
    //         if (shoppingCartItemVO.getGoodsCount() > newBeeMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
    //             return ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult();
    //         }
    //     }
    //     //删除购物项
    //     if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(newBeeMallGoods)) {
    //         //TODO ddd
    //         if (shoppingCartItemDAO.deleteBatch(itemIdList) > 0) {
    //             List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
    //             //TODO dddd
    //             int updateStockNumResult = goodsInfoDAO.updateStockNum(stockNumDTOS);
    //             if (updateStockNumResult < 1) {
    //                 return ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult();
    //             }
    //             //生成订单号
    //             String orderNo = NumberUtil.genOrderNo();
    //             int priceTotal = 0;
    //             //保存订单
    //             FashionMallOrder newBeeMallOrder = new FashionMallOrder();
    //             newBeeMallOrder.setOrderNo(orderNo);
    //             newBeeMallOrder.setUserId(user.getUserId());
    //             newBeeMallOrder.setUserAddress(user.getAddress());
    //             //总价
    //             for (FashionMallShoppingCartItemVO newBeeMallShoppingCartItemVO : myShoppingCartItems) {
    //                 priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
    //             }
    //             if (priceTotal < 1) {
    //                 return ServiceResultEnum.ORDER_PRICE_ERROR.getResult();
    //             }
    //             newBeeMallOrder.setTotalPrice(priceTotal);
    //             //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
    //             String extraInfo = "";
    //             newBeeMallOrder.setExtraInfo(extraInfo);
    //             //生成订单项并保存订单项纪录
    //             //TODO dddd
    //             if (orderDAO.insertSelective(newBeeMallOrder) > 0) {
    //                 //生成所有的订单项快照，并保存至数据库
    //                 List<FashionMallOrderItem> newBeeMallOrderItems = new ArrayList<>();
    //                 for (FashionMallShoppingCartItemVO newBeeMallShoppingCartItemVO : myShoppingCartItems) {
    //                     FashionMallOrderItem newBeeMallOrderItem = new FashionMallOrderItem();
    //                     //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
    //                     BeanUtil.copyProperties(newBeeMallShoppingCartItemVO, newBeeMallOrderItem);
    //                     //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
    //                     newBeeMallOrderItem.setOrderId(newBeeMallOrder.getOrderId());
    //                     newBeeMallOrderItems.add(newBeeMallOrderItem);
    //                 }
    //                 //保存至数据库
    //                 //todo ddddd
    //                 if (orderItemDAO.insertBatch(newBeeMallOrderItems) > 0) {
    //                     //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
    //                     return orderNo;
    //                 }
    //                 return ServiceResultEnum.ORDER_PRICE_ERROR.getResult();
    //             }
    //             return ServiceResultEnum.DB_ERROR.getResult();
    //         }
    //         return ServiceResultEnum.DB_ERROR.getResult();
    //     }
    //     return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    // }


    @Override
    //TODO ???
    public String saveOrder(FashionMallUserVO user, List<FashionMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(FashionMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(FashionMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<FashionMallGoodsInfo> fashionMallGoods = goodsInfoDAO.selectByPrimaryKeys(goodsIds);
        Map<Long, FashionMallGoodsInfo> fashionMallGoodsMap = fashionMallGoods.stream().collect(Collectors.toMap(FashionMallGoodsInfo::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (FashionMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!fashionMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > fashionMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                return ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult();
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(fashionMallGoods)) {
            if (shoppingCartItemDAO.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = goodsInfoDAO.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    return ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult();
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                FashionMallOrder fashionMallOrder = new FashionMallOrder();
                fashionMallOrder.setOrderNo(orderNo);
                fashionMallOrder.setUserId(user.getUserId());
                fashionMallOrder.setUserAddress(user.getAddress());
                //总价
                for (FashionMallShoppingCartItemVO fashionMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += fashionMallShoppingCartItemVO.getGoodsCount() * fashionMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    return ServiceResultEnum.ORDER_PRICE_ERROR.getResult();
                }
                fashionMallOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                fashionMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (orderDAO.insertSelective(fashionMallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<FashionMallOrderItem> fashionMallOrderItems = new ArrayList<>();
                    for (FashionMallShoppingCartItemVO fashionMallShoppingCartItemVO : myShoppingCartItems) {
                        FashionMallOrderItem fashionMallOrderItem = new FashionMallOrderItem();
                        //使用BeanUtil工具类将fashionMallShoppingCartItemVO中的属性复制到fashionMallOrderItem对象中
                        BeanUtil.copyProperties(fashionMallShoppingCartItemVO, fashionMallOrderItem);
                        //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        fashionMallOrderItem.setOrderId(fashionMallOrder.getOrderId());
                        fashionMallOrderItems.add(fashionMallOrderItem);
                    }
                    //保存至数据库
                    if (orderItemDAO.insertBatch(fashionMallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    return ServiceResultEnum.ORDER_PRICE_ERROR.getResult();
                }
                return ServiceResultEnum.DB_ERROR.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }

        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        FashionMallOrder fashionMallOrder = orderDAO.selectByOrderNo(orderNo);
        if (fashionMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            //订单状态判断
            if (orderDAO.closeOrder(Collections.singletonList(fashionMallOrder.getOrderId()), OrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        FashionMallOrder fashionMallOrder = orderDAO.selectByOrderNo(orderNo);
        if (fashionMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            //订单状态判断
            fashionMallOrder.setOrderStatus((byte) OrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            fashionMallOrder.setUpdateTime(new Date());
            if (orderDAO.updateByPrimaryKeySelective(fashionMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public FashionMallOrder getFashionMallOrderByOrderNo(String orderNo) {
        return orderDAO.selectByOrderNo(orderNo);
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        FashionMallOrder fashionMallOrder = orderDAO.selectByOrderNo(orderNo);
        if (fashionMallOrder != null) {
            //todo 订单状态判断 非待支付状态下不进行修改操作
            fashionMallOrder.setOrderStatus((byte) OrderStatusEnum.OREDER_PAID.getOrderStatus());
            fashionMallOrder.setPayType((byte) payType);
            fashionMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            fashionMallOrder.setPayTime(new Date());
            fashionMallOrder.setUpdateTime(new Date());
            if (orderDAO.updateByPrimaryKeySelective(fashionMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<FashionMallOrderItemVO> getOrderItems(Long id) {
        FashionMallOrder fashionMallOrder = orderDAO.selectByPrimaryKey(id);
        if (fashionMallOrder != null) {
            List<FashionMallOrderItem> orderItems = orderItemDAO.selectByOrderId(fashionMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<FashionMallOrderItemVO> fashionMallOrderItemVOS = BeanUtil.copyList(orderItems, FashionMallOrderItemVO.class);
                return fashionMallOrderItemVOS;
            }
        }
        return null;

    }


}
