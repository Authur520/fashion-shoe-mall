package com.lokep.mall.service.impl;

import com.lokep.mall.common.Constants;
import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.controller.vo.FashionMallUserVO;
import com.lokep.mall.dao.FashionMallUserDAO;
import com.lokep.mall.entity.FashionMallUser;
import com.lokep.mall.service.FashionMallUserService;
import com.lokep.mall.util.BeanUtil;
import com.lokep.mall.util.MD5Util;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class FashionMallUserServiceImpl implements FashionMallUserService {

    @Autowired
    private FashionMallUserDAO userDAO;


    @Override
    public PageResult getFashionMallUserPage(PageQueryUtil pageUtil) {
        List<FashionMallUser> usersList = userDAO.findFashionMallUserList(pageUtil);
        int total = userDAO.getTotalFashionMallUser(pageUtil);
        PageResult pageResult = new PageResult(usersList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public boolean lockUsers(Integer[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return userDAO.lockUserBatch(ids, lockStatus) > 0;
    }

    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        FashionMallUser user = userDAO.selectByLoginNameAndPassword(loginName, passwordMD5);
        if (user != null && httpSession != null){
            if (user.getLockedFlag() == 1){
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示
            if (user.getNickName() != null && user.getNickName().length() > 7){
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            FashionMallUserVO userVO = new FashionMallUserVO();
            BeanUtil.copyProperties(user, userVO);
            //设置购物车中的数量
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, userVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public String register(String loginName, String password) {
        if (userDAO.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        FashionMallUser registerUser = new FashionMallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (userDAO.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public FashionMallUserVO updateUserInfo(FashionMallUser mallUser, HttpSession httpSession) {
        FashionMallUser user = userDAO.selectByPrimaryKey(mallUser.getUserId());
        if (user != null) {
            user.setNickName(mallUser.getNickName());
            user.setAddress(mallUser.getAddress());
            user.setIntroduceSign(mallUser.getIntroduceSign());
            if (userDAO.updateByPrimaryKeySelective(user) > 0) {
                FashionMallUserVO newBeeMallUserVO = new FashionMallUserVO();
                user = userDAO.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(user, newBeeMallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, newBeeMallUserVO);
                return newBeeMallUserVO;
            }
        }
        return null;

    }

}
