package com.lokep.mall.dao;

import com.lokep.mall.entity.FashionMallUser;
import com.lokep.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FashionMallUserDAO {
    int deleteByPrimaryKey(Long userId);

    int insert(FashionMallUser record);

    int insertSelective(FashionMallUser record);

    FashionMallUser selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(FashionMallUser record);

    int updateByPrimaryKey(FashionMallUser record);

    List<FashionMallUser> findFashionMallUserList(PageQueryUtil pageUtil);

    int getTotalFashionMallUser(PageQueryUtil pageUtil);

    int lockUserBatch(@Param("ids")Integer[] ids, @Param("lockStatus") int lockStatus);

    FashionMallUser selectByLoginNameAndPassword(@Param("loginName") String loginName, @Param("passwordMD5") String passwordMD5);

    FashionMallUser selectByLoginName(String loginName);
}