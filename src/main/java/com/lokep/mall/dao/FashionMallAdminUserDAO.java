package com.lokep.mall.dao;

import com.lokep.mall.entity.FashionMallAdminUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FashionMallAdminUserDAO {
    // int insert(AdminUser record);
    // int insertSelective(AdminUser record);
    // /**
    //  * 登陆方法
    //  * @param userName
    //  * @param password
    //  * @return
    //  */
    // AdminUser login(@Param("userName") String userName, @Param("password") String password);
    // AdminUser selectByPrimaryKey(Integer adminUserId);
    // int updateByPrimaryKeySelective(AdminUser record);
    // int updateByPrimaryKey(AdminUser record);

    FashionMallAdminUser login(@Param("userName") String userName , @Param("password") String password);

    int deleteByPrimaryKey(Integer adminUserId);

    int insert(FashionMallAdminUser record);

    int insertSelective(FashionMallAdminUser record);

    FashionMallAdminUser selectByPrimaryKey(Integer adminUserId);

    int updateByPrimaryKeySelective(FashionMallAdminUser record);

    int updateByPrimaryKey(FashionMallAdminUser record);
}
