package com.lokep.mall.service;

import com.lokep.mall.entity.FashionMallAdminUser;

public interface FashionMallAdminUserService {

    FashionMallAdminUser login(String userName, String password);

    /**
     *获取用户信息
     * @param loginUserId
     * @return
     */
    FashionMallAdminUser getUserDetailById(Integer loginUserId);

    /**
     *修改管理员基本信息
     * @param loginUserId
     * @param loginUserName
     * @param nickName
     * @return
     */
    boolean updateName(Integer loginUserId, String loginUserName, String nickName);

    /**
     * 修改管理员登陆密码
     * @param loginUserId
     * @param originalPassword
     * @param newPassword
     * @return
     */
    boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword);
}
