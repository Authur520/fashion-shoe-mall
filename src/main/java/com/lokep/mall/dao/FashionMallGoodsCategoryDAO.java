package com.lokep.mall.dao;

import com.lokep.mall.entity.FashionMallGoodsCategory;
import com.lokep.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FashionMallGoodsCategoryDAO {
    int deleteByPrimaryKey(Long categoryId);

    int insert(FashionMallGoodsCategory record);

    int insertSelective(FashionMallGoodsCategory record);

    FashionMallGoodsCategory selectByPrimaryKey(Long categoryId);

    int updateByPrimaryKeySelective(FashionMallGoodsCategory record);

    int updateByPrimaryKey(FashionMallGoodsCategory record);

    List<FashionMallGoodsCategory> findGoodsCategoryList(PageQueryUtil pageUtil);

    int getTotalGoodsCategories(PageQueryUtil pageUtil);

    List<FashionMallGoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("parentIds") List<Long> parentIds, @Param("categoryLevel") int categoryLevel, @Param("number") int number);


    FashionMallGoodsCategory selectByLevelAndName(@Param("parentId") Long parentId, @Param("categoryName") String categoryName);

    int deleteBatch(Integer[] ids);
}