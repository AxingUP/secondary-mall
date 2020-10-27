package cyx.secondary.mall.service;

import cyx.secondary.mall.controller.vo.SecondaryMallUserVO;
import cyx.secondary.mall.entity.MallUser;
import cyx.secondary.mall.util.PageQueryUtil;
import cyx.secondary.mall.util.PageResult;

import javax.servlet.http.HttpSession;

public interface SecondaryMallUserService {

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getSecondaryMallUsersPage(PageQueryUtil pageUtil);

    /**
     * 用户注册
     *
     * @param loginName
     * @param password
     * @return
     */
    String register(String loginName, String password);

    /**
     * 登录
     *
     * @param loginName
     * @param passwordMD5
     * @param httpSession
     * @return
     */
    String login(String loginName, String passwordMD5, HttpSession httpSession);

    /**
     * 用户信息修改并返回最新的用户信息
     *
     * @param mallUser
     * @return
     */
    SecondaryMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession);

    /**
     * 用户禁用与解除禁用(0-未锁定 1-已锁定)
     *
     * @param ids
     * @param lockStatus
     * @return
     */
    Boolean lockUsers(Integer[] ids, int lockStatus);
}
