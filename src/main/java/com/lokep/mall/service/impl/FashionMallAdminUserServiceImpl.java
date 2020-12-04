package com.lokep.mall.service.impl;

import com.lokep.mall.dao.FashionMallAdminUserDAO;
import com.lokep.mall.entity.FashionMallAdminUser;
import com.lokep.mall.service.FashionMallAdminUserService;
import com.lokep.mall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FashionMallAdminUserServiceImpl implements FashionMallAdminUserService {

    @Autowired
    private FashionMallAdminUserDAO adminUserDAO;

    @Override
    public FashionMallAdminUser login(String userName, String password) {
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        return adminUserDAO.login(userName, passwordMD5);
    }

    @Override
    public FashionMallAdminUser getUserDetailById(Integer loginUserId) {
        return adminUserDAO.selectByPrimaryKey(loginUserId);
    }

    @Override
    public boolean updateName(Integer loginUserId, String loginUserName, String nickName) {
        FashionMallAdminUser adminUser = adminUserDAO.selectByPrimaryKey(loginUserId);
        //判断当前用户是否为空
        if (adminUser !=null){
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            if(adminUserDAO.updateByPrimaryKeySelective(adminUser) > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {
        FashionMallAdminUser adminUser = adminUserDAO.selectByPrimaryKey(loginUserId);
        if (adminUser != null){
            String originalPasswordMd5 = MD5Util.MD5Encode(originalPassword,"UTF-8");
            String newPassWordMd5 = MD5Util.MD5Encode(newPassword, "UTF-8");
            adminUser.setLoginPassword(newPassWordMd5);
            if (originalPasswordMd5.equals(adminUser.getLoginPassword())) {
                if (adminUserDAO.updateByPrimaryKeySelective(adminUser) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
