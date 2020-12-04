package com.lokep.mall.controller.mall;

import com.lokep.mall.common.Constants;
import com.lokep.mall.common.IndexConfigTypeEnum;
import com.lokep.mall.controller.vo.FashionMallIndexCarouselVO;
import com.lokep.mall.controller.vo.FashionMallIndexCategoryVO;
import com.lokep.mall.controller.vo.FashionMallIndexConfigGoodsVO;
import com.lokep.mall.service.FashionMallCarouselService;
import com.lokep.mall.service.FashionMallGoodsCategoryService;
import com.lokep.mall.service.FashionMallGoodsIndexConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private FashionMallCarouselService carouselService;

    @Autowired
    private FashionMallGoodsIndexConfigService indexConfigService;

    @Autowired
    private FashionMallGoodsCategoryService categoryService;

    @GetMapping({"/", "/index", "/index.html"})
    public String indexPage(HttpServletRequest request){
        List<FashionMallIndexCategoryVO> categories = categoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)){
            return "您访问的页面出错了！！！";
        }
        List<FashionMallIndexCarouselVO> carousels = carouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<FashionMallIndexConfigGoodsVO> hotGoods = indexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<FashionMallIndexConfigGoodsVO> newGoods = indexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<FashionMallIndexConfigGoodsVO> recommendGoods = indexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMEND.getType(), Constants.INDEX_GOODS_RECOMMEND_NUMBER);
        request.setAttribute("categories", categories);
        request.setAttribute("carousels", carousels);
        request.setAttribute("hotGoodses", hotGoods);
        request.setAttribute("newGoodses", newGoods);
        request.setAttribute("recommendGoodses", recommendGoods);
        return "mall/index";
    }
}
