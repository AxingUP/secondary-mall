package cyx.secondary.mall.service;

import cyx.secondary.mall.controller.vo.AdminUserVO;
import cyx.secondary.mall.entity.AdminUser;

import javax.servlet.http.HttpSession;

public interface AdminUserService {

//    管理员登录
    String login(String loginName, String passwordMD5, HttpSession httpSession);

    AdminUser getUserDetailById(Long adminUserId);

    Boolean updatePassword(Long adminUserId, String originalPassword, String newPassword);

    Boolean updateName(Long adminUserId, String loginUserName, String nickName);

    AdminUserVO updateUserInfo( AdminUser adminUser,HttpSession httpSession);
}
