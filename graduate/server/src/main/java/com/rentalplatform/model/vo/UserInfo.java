package com.rentalplatform.model.vo;

import com.rentalplatform.model.entity.SysUser;
import lombok.Builder;
import lombok.Data;

/** 用户信息出参：不含 password / idCard 等敏感字段（openapi.yaml UserInfo） */
@Data
@Builder
public class UserInfo {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    /** 已绑定时返回脱敏值，如 138****5678 */
    private String phone;
    private String realName;
    private String role;
    private String orgName;
    private String status;

    public static UserInfo from(SysUser user) {
        return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(maskPhone(user.getPhone()))
                .realName(user.getRealName())
                .role(user.getRole())
                .orgName(null)
                .status(user.getStatus())
                .build();
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
