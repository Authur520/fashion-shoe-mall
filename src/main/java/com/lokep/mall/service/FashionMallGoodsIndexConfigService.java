package com.lokep.mall.service;

import com.lokep.mall.controller.vo.FashionMallIndexConfigGoodsVO;
import com.lokep.mall.entity.FashionMallIndexConfig;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;

import java.util.List;

public interface FashionMallGoodsIndexConfigService {

    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String saveIndexConfig(FashionMallIndexConfig indexConfig);

    String updateIndexConfig(FashionMallIndexConfig indexConfig);

    boolean deleteBatch(Long[] ids);

    /**
     * 返回固定数量的首页配置商品对象(首页调用)
     * @param number
     * @return
     */
    List<FashionMallIndexConfigGoodsVO> getConfigGoodsForIndex(int type, int number);
}
