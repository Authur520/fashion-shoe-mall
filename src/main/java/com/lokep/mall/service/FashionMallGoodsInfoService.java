package com.lokep.mall.service;

import com.lokep.mall.entity.FashionMallGoodsInfo;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;

public interface FashionMallGoodsInfoService {

    /**
     * 获取商品详情
     * @param id
     * @return
     */

    FashionMallGoodsInfo getFashionMallGoodsById(Long id);

    /**
     *后台分页
     *@param pageUtil
     *@return
     * */
    PageResult getFashionMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     * @param goodsInfo
     * @return
     */
    String saveGoodsInfo(FashionMallGoodsInfo goodsInfo);

    /**
     * 修改商品
     * @param goodsInfo
     * @return
     */
    String updateGoodsInfo(FashionMallGoodsInfo goodsInfo);

    /**
     * 商品的上架下架
     * @param ids
     * @param sellStatus
     * @return
     */
    boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    /**
     * 搜索商品
     * @param pageUtil
     * @return
     */
    PageResult searchFashionMallGoods(PageQueryUtil pageUtil);
}
