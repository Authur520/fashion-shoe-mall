package com.lokep.mall.service.impl;

import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.controller.vo.FashionMallSearchGoodsVO;
import com.lokep.mall.dao.FashionMallGoodsInfoDAO;
import com.lokep.mall.entity.FashionMallGoodsInfo;
import com.lokep.mall.service.FashionMallGoodsInfoService;
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
public class FashionMallGoodsInfoServiceImpl implements FashionMallGoodsInfoService {

    @Autowired
    private FashionMallGoodsInfoDAO goodsInfoDAO;


    @Override
    public FashionMallGoodsInfo getFashionMallGoodsById(Long id) {
        return goodsInfoDAO.selectByPrimaryKey(id);
    }

    @Override
    public PageResult getFashionMallGoodsPage(PageQueryUtil pageUtil) {
        List<FashionMallGoodsInfo> goodsList = goodsInfoDAO.findFashionMallGoodsList(pageUtil);
        int total = goodsInfoDAO.getTotalFashionMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList,total,pageUtil.getLimit(),pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveGoodsInfo(FashionMallGoodsInfo goodsInfo) {
        if (goodsInfoDAO.insertSelective(goodsInfo) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }else {
            return ServiceResultEnum.DB_ERROR.getResult();
        }
    }

    @Override
    public String updateGoodsInfo(FashionMallGoodsInfo goodsInfo) {
        FashionMallGoodsInfo temp = goodsInfoDAO.selectByPrimaryKey(goodsInfo.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goodsInfo.setUpdateTime(new Date());
        if (goodsInfoDAO.updateByPrimaryKeySelective(goodsInfo) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsInfoDAO.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchFashionMallGoods(PageQueryUtil pageUtil) {
        List<FashionMallGoodsInfo> goodsList = goodsInfoDAO.findNewBeeMallGoodsListBySearch(pageUtil);
        int total = goodsInfoDAO.getTotalNewBeeMallGoodsBySearch(pageUtil);
        List<FashionMallSearchGoodsVO> searchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            searchGoodsVOS = BeanUtil.copyList(goodsList, FashionMallSearchGoodsVO.class);
            for (FashionMallSearchGoodsVO newBeeMallSearchGoodsVO : searchGoodsVOS) {
                String goodsName = newBeeMallSearchGoodsVO.getGoodsName();
                String goodsIntro = newBeeMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    newBeeMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    newBeeMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(searchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
