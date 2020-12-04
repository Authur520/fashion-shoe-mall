package com.lokep.mall.service;

import com.lokep.mall.controller.vo.FashionMallIndexCarouselVO;
import com.lokep.mall.entity.FashionMallCarousel;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;

import java.util.List;

public interface FashionMallCarouselService {

    /**
     * 后台分页
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    /**
     * 添加功能
     * @param carousel
     * @return
     */
    String saveCarousel(FashionMallCarousel carousel);

    String updateCarousel(FashionMallCarousel carousel);

    boolean deleteBatch(Integer[] ids);

    /**
     * 返回固定数量的轮播图对象(首页调用)
     * @param indexCategoryNumber
     * @return
     */
    List<FashionMallIndexCarouselVO> getCarouselsForIndex(int indexCategoryNumber);

    FashionMallCarousel getCarouselById(Integer id);
}
