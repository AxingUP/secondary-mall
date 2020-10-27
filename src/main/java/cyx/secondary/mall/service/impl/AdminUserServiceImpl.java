package cyx.secondary.mall.service.impl;

import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.controller.vo.AdminUserVO;
import cyx.secondary.mall.controller.vo.SecondaryMallUserVO;
import cyx.secondary.mall.dao.AdminUserMapper;
import cyx.secondary.mall.entity.AdminUser;
import cyx.secondary.mall.entity.MallUser;
import cyx.secondary.mall.service.AdminUserService;
import cyx.secondary.mall.util.BeanUtil;
import cyx.secondary.mall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

//    管理员登录
@Override
public String login(String loginName, String passwordMD5, HttpSession httpSession) {
    AdminUser user = adminUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
    System.out.println(user);
    if (user != null && httpSession != null) {
        if (user.getLocked() == 1) {
            return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
        }
        //昵称太长 影响页面展示
        if (user.getNickName() != null && user.getNickName().length() > 7) {
            String tempNickName = user.getNickName().substring(0, 7) + "..";
            user.setNickName(tempNickName);
        }
        AdminUserVO adminUserVO = new AdminUserVO();
        BeanUtil.copyProperties(user,adminUserVO);
        httpSession.setAttribute(Constants.ADMIN_USER_SESSION_KEY, adminUserVO);
        return ServiceResultEnum.SUCCESS.getResult();
    }
    return ServiceResultEnum.LOGIN_ERROR.getResult();
}

//    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
//        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
//        if (user != null && httpSession != null) {
//            if (user.getLockedFlag() == 1) {
//                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
//            }
//            //昵称太长 影响页面展示
//            if (user.getNickName() != null && user.getNickName().length() > 7) {
//                String tempNickName = user.getNickName().substring(0, 7) + "..";
//                user.setNickName(tempNickName);
//            }
//            SecondaryMallUserVO secondaryMallUserVO = new SecondaryMallUserVO();
//            BeanUtil.copyProperties(user, secondaryMallUserVO);
//            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, secondaryMallUserVO);
//            return ServiceResultEnum.SUCCESS.getResult();
//        }
//        return ServiceResultEnum.LOGIN_ERROR.getResult();
//    }

//    @Override
//    public AdminUser login(String userName, String password) {
//        String passwordMd5 = MD5Util.MD5Encode(password, "UTF-8");
//        return adminUserMapper.login(userName, passwordMd5);
//    }

    @Override
    public AdminUser getUserDetailById(Long adminUserId) {
        return adminUserMapper.selectByPrimaryKey(adminUserId);
    }

    @Override
    public Boolean updatePassword(Long adminUserId, String originalPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
        System.out.println(adminUser);
//        当前用户非空才允许进行下一步更改
        if (adminUser != null) {
            String originalPasswordMd5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
            String newPasswordMd5 = MD5Util.MD5Encode(newPassword, "UTF-8");
//            比较原密码是否正确
            if (originalPasswordMd5.equals(adminUser.getLoginPassword())) {
//                设置新密码
                adminUser.setLoginPassword(newPasswordMd5);
                if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) {
//                    修改成功返回true
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public Boolean updateName(Long adminUserId, String loginUserName, String nickName){
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
//        当前用户非空才可以进行更改
        if(adminUser != null){
        adminUser.setLoginUserName(loginUserName);
        adminUser.setNickName(nickName);
        if(adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0){
//            修改成功则返回true
            return true;
            }
        }
        return false;
    }

    @Override
    public AdminUserVO updateUserInfo(AdminUser adminUser, HttpSession httpSession) {
        AdminUser user = adminUserMapper.selectByPrimaryKey(adminUser.getAdminUserId());
        if (user != null) {
                AdminUserVO adminUserVO = new AdminUserVO();
                user = adminUserMapper.selectByPrimaryKey(adminUser.getAdminUserId());
                BeanUtil.copyProperties(user, adminUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, adminUserVO);
                return adminUserVO;
            }
        return null;
    }
}

