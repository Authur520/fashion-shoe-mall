package com.lokep.mall.service.impl;

import com.lokep.mall.common.Constants;
import com.lokep.mall.common.GoodsCategoryEnum;
import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.controller.vo.FashionMallIndexCategoryVO;
import com.lokep.mall.controller.vo.SearchPageCategoryVO;
import com.lokep.mall.controller.vo.SecondLevelCategoryVO;
import com.lokep.mall.controller.vo.ThirdLevelCategoryVO;
import com.lokep.mall.dao.FashionMallGoodsCategoryDAO;
import com.lokep.mall.entity.FashionMallGoodsCategory;
import com.lokep.mall.service.FashionMallGoodsCategoryService;
import com.lokep.mall.util.BeanUtil;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class FashionMallGoodsCategoryServiceImpl implements FashionMallGoodsCategoryService {

    @Autowired
    private FashionMallGoodsCategoryDAO goodsCategoryDAO;

    @Override
    public PageResult getCategoriesPage(PageQueryUtil pageUtil) {
        List<FashionMallGoodsCategory> goodsCategories = goodsCategoryDAO.findGoodsCategoryList(pageUtil);
        int total = goodsCategoryDAO.getTotalGoodsCategories(pageUtil);
        PageResult pageResult = new PageResult(goodsCategories,total,pageUtil.getLimit(),pageUtil.getPage());
        return pageResult;
    }

    @Override
    public FashionMallGoodsCategory getGoodsCategoryById(Long id) {
        return goodsCategoryDAO.selectByPrimaryKey(id);
    }

    @Override
    public List<FashionMallGoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return goodsCategoryDAO.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0);//0代表查询所有
    }

    @Override
    public String saveGoodsCategory(FashionMallGoodsCategory goodsCategory) {
        FashionMallGoodsCategory temp = goodsCategoryDAO.selectByLevelAndName(goodsCategory.getParentId(), goodsCategory.getCategoryName());
        if (temp != null){
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if (goodsCategoryDAO.insertSelective(goodsCategory) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoodsCategory(FashionMallGoodsCategory goodsCategory) {
        FashionMallGoodsCategory temp = goodsCategoryDAO.selectByPrimaryKey(goodsCategory.getCategoryId());
        if (temp == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        // FashionMallGoodsCategory temp2 = goodsCategoryDAO.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        // if (temp2 != null && temp2.getCategoryId().equals(goodsCategory.getCategoryId())){
        //     //同名且不同id 不能继续修改
        //     return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        // }
        goodsCategory.setUpdateTime(new Date());
        if (goodsCategoryDAO.updateByPrimaryKeySelective(goodsCategory)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1){
            return false;
        }
        //删除分类数据
        return goodsCategoryDAO.deleteBatch(ids) > 0;
    }

    @Override
    public List<FashionMallIndexCategoryVO> getCategoriesForIndex() {
        List<FashionMallIndexCategoryVO> newBeeMallIndexCategoryVOS = new ArrayList<>();
        //获取一级分类的固定数量的数据
        List<FashionMallGoodsCategory> firstLevelCategories = goodsCategoryDAO.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), GoodsCategoryEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(FashionMallGoodsCategory::getCategoryId).collect(Collectors.toList());
            //获取二级分类的数据
            List<FashionMallGoodsCategory> secondLevelCategories = goodsCategoryDAO.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds, GoodsCategoryEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream().map(FashionMallGoodsCategory::getCategoryId).collect(Collectors.toList());
                //获取三级分类的数据
                List<FashionMallGoodsCategory> thirdLevelCategories = goodsCategoryDAO.selectByLevelAndParentIdsAndNumber(secondLevelCategoryIds, GoodsCategoryEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    //根据 parentId 将 thirdLevelCategories 分组
                    Map<Long, List<FashionMallGoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(FashionMallGoodsCategory::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    //处理二级分类
                    for (FashionMallGoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        //如果该二级分类下有数据则放入 secondLevelCategoryVOS 对象中
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                            //根据二级分类的id取出thirdLevelCategoryMap分组中的三级分类list
                            List<FashionMallGoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS((BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class)));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    //处理一级分类
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        //根据 parentId 将 thirdLevelCategories 分组
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (FashionMallGoodsCategory firstCategory : firstLevelCategories) {
                            FashionMallIndexCategoryVO newBeeMallIndexCategoryVO = new FashionMallIndexCategoryVO();
                            BeanUtil.copyProperties(firstCategory, newBeeMallIndexCategoryVO);
                            //如果该一级分类下有数据则放入 newBeeMallIndexCategoryVOS 对象中
                            if (secondLevelCategoryVOMap.containsKey(firstCategory.getCategoryId())) {
                                //根据一级分类的id取出secondLevelCategoryVOMap分组中的二级级分类list
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getCategoryId());
                                newBeeMallIndexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                newBeeMallIndexCategoryVOS.add(newBeeMallIndexCategoryVO);
                            }
                        }
                    }
                }
            }
            return newBeeMallIndexCategoryVOS;
        } else {
            return null;
        }
    }

    @Override
    public SearchPageCategoryVO getCategoriesForSearch(Long categoryId) {
        SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
        FashionMallGoodsCategory thirdLevelGoodsCategory = goodsCategoryDAO.selectByPrimaryKey(categoryId);
        if (thirdLevelGoodsCategory != null && thirdLevelGoodsCategory.getCategoryLevel() == GoodsCategoryEnum.LEVEL_THREE.getLevel()) {
            //获取当前三级分类的二级分类
            FashionMallGoodsCategory secondLevelGoodsCategory = goodsCategoryDAO.selectByPrimaryKey(thirdLevelGoodsCategory.getParentId());
            if (secondLevelGoodsCategory != null && secondLevelGoodsCategory.getCategoryLevel() == GoodsCategoryEnum.LEVEL_TWO.getLevel()) {
                //获取当前二级分类下的三级分类List
                List<FashionMallGoodsCategory> thirdLevelCategories = goodsCategoryDAO.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelGoodsCategory.getCategoryId()), GoodsCategoryEnum.LEVEL_THREE.getLevel(), Constants.SEARCH_CATEGORY_NUMBER);
                searchPageCategoryVO.setCurrentCategoryName(thirdLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setSecondLevelCategoryName(secondLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setThirdLevelCategoryList(thirdLevelCategories);
                return searchPageCategoryVO;
            }
        }
        return null;
    }


}
