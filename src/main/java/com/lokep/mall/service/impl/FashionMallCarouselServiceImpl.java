package com.lokep.mall.service.impl;

import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.controller.vo.FashionMallIndexCarouselVO;
import com.lokep.mall.dao.FashionMallCarouselDAO;
import com.lokep.mall.entity.FashionMallCarousel;
import com.lokep.mall.service.FashionMallCarouselService;
import com.lokep.mall.util.BeanUtil;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FashionMallCarouselServiceImpl implements FashionMallCarouselService {

    @Autowired
    private FashionMallCarouselDAO carouselDAO;


    @Override
    public PageResult getCarouselPage(PageQueryUtil pageUtil) {
        List<FashionMallCarousel> carousels = carouselDAO.findCarouselList(pageUtil);
        int total = carouselDAO.getTotalCarousels(pageUtil);
        PageResult pageResult = new PageResult(carousels, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCarousel(FashionMallCarousel carousel) {
        if (carouselDAO.insertSelective(carousel) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCarousel(FashionMallCarousel carousel) {
        FashionMallCarousel temp =carouselDAO.selectByPrimaryKey(carousel.getCarouselId());
        if (temp == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setCarouselRank(carousel.getCarouselRank());
        temp.setRedirectUrl(carousel.getRedirectUrl());
        temp.setCarouselUrl(carousel.getCarouselUrl());
        temp.setUpdateTime(new Date());
        if (carouselDAO.updateByPrimaryKey(temp)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1){
            return false;
        }
        //删除数据
        return carouselDAO.deleteBatch(ids) > 0;
    }

    @Override
    public List<FashionMallIndexCarouselVO> getCarouselsForIndex(int number) {
        List<FashionMallIndexCarouselVO> fashionMallIndexCarouselVOS = new ArrayList<>(number);
        List<FashionMallCarousel> carousels = carouselDAO.findCarouselsByNum(number);
        if (!CollectionUtils.isEmpty(carousels)){
            fashionMallIndexCarouselVOS = BeanUtil.copyList(carousels, FashionMallIndexCarouselVO.class);
        }
        return fashionMallIndexCarouselVOS;
    }

    @Override
    public FashionMallCarousel getCarouselById(Integer id) {
        return carouselDAO.selectByPrimaryKey(id);
    }
}
