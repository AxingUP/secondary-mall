package cyx.secondary.mall.controller.admin;


import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.controller.vo.AdminUserVO;
import cyx.secondary.mall.entity.AdminUser;
import cyx.secondary.mall.service.AdminUserService;
import cyx.secondary.mall.util.MD5Util;
import cyx.secondary.mall.util.Result;
import cyx.secondary.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminUserService adminUserService;

    @GetMapping({"/login"})
    public String login() {
        return "admin/login";
    }

//    管理员登录
@PostMapping("/login")
@ResponseBody
public Result login(@RequestParam("loginName") String loginName,
                    @RequestParam("verifyCode") String verifyCode,
                    @RequestParam("password") String password,
                    HttpSession httpSession) {
    if (StringUtils.isEmpty(loginName)) {
        httpSession.setAttribute("errorMsg", "用户名不能为空");
        return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
    }
    if (StringUtils.isEmpty(password)) {
        httpSession.setAttribute("errorMsg", "密码不能为空");
        return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
    }
    if (StringUtils.isEmpty(verifyCode)) {
        httpSession.setAttribute("errorMsg", "验证码不能为空");
        return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
    }
    String kaptchaCode = httpSession.getAttribute("verifyCode")+ "";
    if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
        return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
    }
    String loginResult = adminUserService.login(loginName, MD5Util.MD5Encode(password, "UTF-8"), httpSession);
    //登录成功
    if (ServiceResultEnum.SUCCESS.getResult().equals(loginResult)) {
        return ResultGenerator.genSuccessResult();
    }
    //登录失败
    return ResultGenerator.genFailResult(loginResult);
}
//    @PostMapping(value = "/login")
//    public String login(@RequestParam("userName") String userName,
//                        @RequestParam("password") String password,
//                        @RequestParam("verifyCode") String verifyCode,
//                        HttpSession session) {
//        if (StringUtils.isEmpty(verifyCode)) {
//            session.setAttribute("errorMsg", "验证码不能为空");
//            return "admin/login";
//        }
//        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
//            session.setAttribute("errorMsg", "用户名或密码不能为空");
//            return "admin/login";
//        }
//        String kaptchaCode = session.getAttribute("verifyCode") + "";
//        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
//            session.setAttribute("errorMsg", "验证码错误");
//            return "admin/login";
//        }
//        AdminUser adminUser = adminUserService.login(userName, password);
//        if (adminUser != null) {
//            session.setAttribute("loginUser", adminUser.getNickName());
//            session.setAttribute("loginUserId", adminUser.getAdminUserId());
//            //session过期时间设置为7200秒 即两小时
//            //session.setMaxInactiveInterval(60 * 60 * 2);
//            return "redirect:/admin/index";
//        } else {
//            session.setAttribute("errorMsg", "登录信息错误");
//            return "admin/login";
//        }
//    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request) {
//        Long loginUserId = (Long) request.getSession().getAttribute("adminUserId");
        AdminUserVO adminUserVO = (AdminUserVO)request.getSession().getAttribute("adminUser");
        Long loginUserId = adminUserVO.getAdminUserId();
        System.out.println(loginUserId);
        AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
        if (adminUser == null) {
            return "admin/login";
        }
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName", adminUser.getLoginUserName());
        request.setAttribute("nickName", adminUser.getNickName());
        return "admin/profile";
    }

    @PostMapping("/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword) {
        if(StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)){
            return "密码不能为空";
        }
//        Long loginUserId = (Long) request.getSession().getAttribute("loginUserId");
        AdminUserVO adminUserVO = (AdminUserVO)request.getSession().getAttribute("adminUser");
        Long loginUserId = adminUserVO.getAdminUserId();
        if(adminUserService.updatePassword(loginUserId,originalPassword,newPassword)){
        //修改成功后清空session中的数据，前端控制跳转至登录页
        request.getSession().removeAttribute("loginUserId");
        request.getSession().removeAttribute("loginUser");
        request.getSession().removeAttribute("errorMsg");
        return "success";
        }else {
            return "修改失败";
        }
    }
    @PostMapping("/profile/name")
    @ResponseBody
    public String nameUpdate(HttpServletRequest request, @RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName){
        if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)) {
            return "参数不能为空";
        }
//        Long loginUserId = (Long) request.getSession().getAttribute("loginUserId");
        AdminUserVO adminUserVO = (AdminUserVO)request.getSession().getAttribute("adminUser");
        Long loginUserId = adminUserVO.getAdminUserId();
        if (adminUserService.updateName(loginUserId, loginUserName, nickName)) {
            return "success";
        } else {
            return "修改失败";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("adminUser");
        return "admin/login";
    }

    @GetMapping({"/index"})
    public String index() {
        return "admin/index";
    }

}
