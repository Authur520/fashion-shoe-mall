package com.lokep.mall.service.impl;

import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.controller.vo.FashionMallIndexConfigGoodsVO;
import com.lokep.mall.dao.FashionMallGoodsInfoDAO;
import com.lokep.mall.dao.FashionMallIndexConfigDAO;
import com.lokep.mall.entity.FashionMallGoodsInfo;
import com.lokep.mall.entity.FashionMallIndexConfig;
import com.lokep.mall.service.FashionMallGoodsIndexConfigService;
import com.lokep.mall.util.BeanUtil;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FashionMallGoodsIndexConfigServiceImpl implements FashionMallGoodsIndexConfigService {

    @Autowired
    private FashionMallIndexConfigDAO indexConfigDAO;

    @Autowired
    private FashionMallGoodsInfoDAO goodsInfoDAO;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<FashionMallIndexConfig> indexConfigs = indexConfigDAO.findIndexConfigsList(pageUtil);
        int total = indexConfigDAO.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(FashionMallIndexConfig indexConfig) {
        //判断是否存在此商品
        if (indexConfigDAO.insertSelective(indexConfig) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(FashionMallIndexConfig indexConfig) {
        FashionMallIndexConfig temp = indexConfigDAO.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigDAO.updateByPrimaryKeySelective(indexConfig) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }else {
            return ServiceResultEnum.DB_ERROR.getResult();
        }
    }

    @Override
    public boolean deleteBatch(Long[] ids) {
        if (ids.length < 1){
            return false;
        }
        //删除数据
        return indexConfigDAO.deleteBatch(ids) > 0;
    }

    @Override
    public List<FashionMallIndexConfigGoodsVO> getConfigGoodsForIndex(int type, int number) {
        List<FashionMallIndexConfigGoodsVO> indexConfigGoodsVOS = new ArrayList<>(number);
        List<FashionMallIndexConfig> indexConfigs =indexConfigDAO.findIndexConfigsByTypeAndNum(type, number);
        if (!CollectionUtils.isEmpty(indexConfigs)){
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigs.stream().map(FashionMallIndexConfig::getGoodsId).collect(Collectors.toList());
            System.out.println(goodsIds);
            List<FashionMallGoodsInfo> goodsInfos = goodsInfoDAO.selectByPrimaryKeys(goodsIds);
            indexConfigGoodsVOS = BeanUtil.copyList(goodsInfos, FashionMallIndexConfigGoodsVO.class);
            for (FashionMallIndexConfigGoodsVO IndexConfigGoodsVO : indexConfigGoodsVOS) {
                String goodsName = IndexConfigGoodsVO.getGoodsName();
                String goodsIntro = IndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    IndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    IndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return indexConfigGoodsVOS;
    }

}
