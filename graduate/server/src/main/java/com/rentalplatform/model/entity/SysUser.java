package com.rentalplatform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 与表 sys_user 一一对应（V1__schema.sql），不含业务方法。 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String openid;
    private String unionid;
    private String username;
    private String password;
    private String phone;
    private String nickname;
    private String avatar;
    private String realName;
    /** AES 加密存储，任何接口不得直接返回 */
    private String idCard;
    private String role;
    private Long orgId;
    private String regionCode;
    private String status;
    /** 改密/改角色/禁用时 +1，令旧令牌立即失效（技术方案 §5.1） */
    private Integer tokenVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
