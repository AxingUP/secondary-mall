package cyx.secondary.mall.service.impl;

import cyx.secondary.mall.common.Constants;
import cyx.secondary.mall.common.ServiceResultEnum;
import cyx.secondary.mall.controller.vo.SecondaryMallUserVO;
import cyx.secondary.mall.dao.MallUserMapper;
import cyx.secondary.mall.entity.MallUser;
import cyx.secondary.mall.service.SecondaryMallUserService;
import cyx.secondary.mall.util.BeanUtil;
import cyx.secondary.mall.util.MD5Util;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Service
public class SecondaryMallUserServiceImpl implements SecondaryMallUserService {
    @Autowired
    private MallUserMapper mallUserMapper;

    @Override
    public PageResult getSecondaryMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

//  用户注册
    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        registerUser.setCreateTime(new Date());
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

//    用户登录
    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        System.out.println(user);
        if (user != null && httpSession != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            SecondaryMallUserVO secondaryMallUserVO = new SecondaryMallUserVO();
            BeanUtil.copyProperties(user, secondaryMallUserVO);
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, secondaryMallUserVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public SecondaryMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
        if (user != null) {
            user.setNickName(mallUser.getNickName());
            user.setAddress(mallUser.getAddress());
            user.setIntroduceSign(mallUser.getIntroduceSign());
            if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
                SecondaryMallUserVO secondaryMallUserVO = new SecondaryMallUserVO();
                user = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(user, secondaryMallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, secondaryMallUserVO);
                return secondaryMallUserVO;
            }
        }
        return null;
    }

    @Override
    public Boolean lockUsers(Integer[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
