package com.eshop.enums;

/**
 * Created by windvalley on 2018/7/14.
 */
public enum UserRoleEnum {
    CUSTOMER(0, "CUSTOMER"),
    ADMIN(1, "ADMIN"),
    ;

    private final int code;

    private final String roleDescript;

    UserRoleEnum(int code, String roleDescript) {
        this.code = code;
        this.roleDescript = roleDescript;
    }

    public int getCode() {
        return code;
    }

    public String getRoleDescript() {
        return roleDescript;
    }
}
