package com.lokep.mall.service;

import com.lokep.mall.controller.vo.FashionMallIndexCategoryVO;
import com.lokep.mall.controller.vo.SearchPageCategoryVO;
import com.lokep.mall.entity.FashionMallGoodsCategory;
import com.lokep.mall.entity.FashionMallGoodsInfo;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;

import java.util.List;


public interface FashionMallGoodsCategoryService {
    /**
     * 后台分页
     * */
    PageResult getCategoriesPage(PageQueryUtil pageUtil);

    FashionMallGoodsCategory getGoodsCategoryById(Long id);

    /**
     * 根据parentId和level获取分类列表
     *
     * @param parentIds
     * @param categoryLevel
     * @return
     */
    List<FashionMallGoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);

    String saveGoodsCategory(FashionMallGoodsCategory goodsCategory);

    String updateGoodsCategory(FashionMallGoodsCategory goodsCategory);

    boolean deleteBatch(Integer[] ids);

    /**
     * 返回分类数据(首页调用)
     * @return
     */
    List<FashionMallIndexCategoryVO> getCategoriesForIndex();

    /**
     *返回分类数据(搜索页调用)
     * @param categoryId
     * @return
     */
    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);
}
