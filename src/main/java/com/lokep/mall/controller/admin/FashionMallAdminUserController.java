package com.lokep.mall.controller.admin;

import com.lokep.mall.common.ServiceResultEnum;
import com.lokep.mall.entity.FashionMallAdminUser;
import com.lokep.mall.entity.FashionMallUser;
import com.lokep.mall.service.FashionMallAdminUserService;
import com.lokep.mall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class FashionMallAdminUserController {

    @Autowired
    private FashionMallAdminUserService adminUserService;

    @GetMapping("/login")
    public String login(){
        return "admin/login";
    }

    @GetMapping("/index")
    public String index(HttpServletRequest request){
        request.setAttribute("path","index");
        return "admin/index";
    }
    /**
     * 登陆后台管理系统
     * @param userName
     * @param password
     * @param verifyCode
     * @param session
     * @return
     */
    @PostMapping("/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session
                        ){
        if (StringUtils.isEmpty(verifyCode)){
            session.setAttribute("errorMsg","验证码不能为空");
            return "admin/login";
        }
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            session.setAttribute("errorMsg","用户名或密码不能为空");
            return "admin/login";
        }
        String kaptchaCode = session.getAttribute("verifyCode") + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            session.setAttribute("errorMsg", "验证码错误");
            return "admin/login";
        }
        FashionMallAdminUser adminUser = adminUserService.login(userName, password);
        if(adminUser != null){
            session.setAttribute("loginUser", adminUser.getNickName());
            session.setAttribute("loginUserId", adminUser.getAdminUserId());
            return "redirect:/admin/index";
        }
        else {
            session.setAttribute("errorMsg", "登陆失败，请重新输入账号");
            return "/admin/login";
        }
    }

    /**
     * 修改密码模块
     * @param request
     * @return
     */
    @GetMapping("/profile")
    public String profilePage(HttpServletRequest request){
        Integer loginUserId = (Integer) request.getSession().getAttribute("loginUserId");
        FashionMallAdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
        if (adminUser == null){
            return "admin/login";
        }
        request.setAttribute("path","profile");
        request.setAttribute("loginUserName",adminUser.getLoginUserName());
        request.setAttribute("nickName",adminUser.getNickName());
        return "admin/profile";
    }

    //修改基本信息
    @RequestMapping(value = "/profile/name", method = RequestMethod.POST)
    @ResponseBody
    public String updateName(HttpServletRequest request, @RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName){
        if (StringUtils.isEmpty(loginUserName)||StringUtils.isEmpty(nickName)){
            return "参数异常！！！";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updateName(loginUserId, loginUserName, nickName)){
            return ServiceResultEnum.SUCCESS.getResult();
        }else {
            return "修改失败";
        }
    }

    @RequestMapping(value = "/profile/password",method = RequestMethod.POST)
    @ResponseBody
    public String updatePassword(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword){
        if (StringUtils.isEmpty(originalPassword)||StringUtils.isEmpty(newPassword)){
            return "参数异常！！！";
        }
        Integer loginUserId = (Integer) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updatePassword(loginUserId, originalPassword, newPassword)){
            //TODO 修改成功后清空session中的数据，前端控制跳转至登录页
            return "修改成功";
        }else {
            return "修改失败";
        }
    }


    /**
     *安全退出
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("loginUserId");
        request.getSession().removeAttribute("loginUser");
        request.getSession().removeAttribute("errorMsg");
        return "admin/login";
    }

}
