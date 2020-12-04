package com.lokep.mall.dao;

import com.lokep.mall.entity.FashionMallCarousel;
import com.lokep.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FashionMallCarouselDAO {
    int deleteByPrimaryKey(Integer carouselId);

    int insert(FashionMallCarousel record);

    int insertSelective(FashionMallCarousel record);

    FashionMallCarousel selectByPrimaryKey(Integer carouselId);

    int updateByPrimaryKeySelective(FashionMallCarousel record);

    int updateByPrimaryKey(FashionMallCarousel record);

    List<FashionMallCarousel> findCarouselList(PageQueryUtil pageUtil);

    int getTotalCarousels(PageQueryUtil pageUtil);

    int deleteBatch(Integer[] ids);

    List<FashionMallCarousel> findCarouselsByNum(@Param("number") int number);
}