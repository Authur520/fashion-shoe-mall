package com.lokep.mall.service;

import com.lokep.mall.controller.vo.FashionMallUserVO;
import com.lokep.mall.entity.FashionMallUser;
import com.lokep.mall.util.PageQueryUtil;
import com.lokep.mall.util.PageResult;

import javax.servlet.http.HttpSession;

public interface FashionMallUserService {


    /**
     * 后台分页
     * @param pageUtil
     * @return
     */
    PageResult getFashionMallUserPage(PageQueryUtil pageUtil);

    /**
     * 用户禁用与解除禁用(0-未锁定 1-已锁定)
     * @param ids
     * @param lockStatus
     * @return
     */
    boolean lockUsers(Integer[] ids, int lockStatus);

    /**
     * 门户页面会员的登陆
     * @param loginName
     * @param passwordMD5
     * @param httpSession
     * @return
     */
    String login(String loginName, String passwordMD5, HttpSession httpSession);

    /**
     * 注册会员
     * @param loginName
     * @param password
     * @return
     */
    String register(String loginName, String password);

    /**
     * 用户信息修改并返回最新的用户信息
     * @param mallUser
     * @return
     */
    FashionMallUserVO updateUserInfo(FashionMallUser mallUser, HttpSession httpSession);
}
