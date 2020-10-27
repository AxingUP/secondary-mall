package cyx.secondary.mall.controller.vo;

import java.io.Serializable;

public class AdminUserVO implements Serializable {
    private Long adminUserId;

    private String nickName;

    private String loginUserName;

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    @Override
    public String toString() {
        return "AdminUserVO{" +
                "adminUserId=" + adminUserId +
                ", nickName='" + nickName + '\'' +
                ", loginUserName='" + loginUserName + '\'' +
                '}';
    }
}


