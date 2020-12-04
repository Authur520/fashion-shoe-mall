package com.lokep.mall.dao;

import com.lokep.mall.entity.FashionMallIndexConfig;
import com.lokep.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FashionMallIndexConfigDAO {
    int deleteByPrimaryKey(Long configId);

    int insert(FashionMallIndexConfig record);

    int insertSelective(FashionMallIndexConfig record);

    FashionMallIndexConfig selectByPrimaryKey(Long configId);

    int updateByPrimaryKeySelective(FashionMallIndexConfig record);

    int updateByPrimaryKey(FashionMallIndexConfig record);

    List<FashionMallIndexConfig> findIndexConfigsList(PageQueryUtil pageUtil);

    int getTotalIndexConfigs(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids);

    //TODO 参数@Param("configType")
    List<FashionMallIndexConfig> findIndexConfigsByTypeAndNum(@Param("type") int type, @Param("number") int number);
}