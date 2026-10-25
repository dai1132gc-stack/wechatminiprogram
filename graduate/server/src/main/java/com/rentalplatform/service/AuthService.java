package com.rentalplatform.service;

import com.rentalplatform.model.dto.LoginDTO;
import com.rentalplatform.model.vo.LoginVO;
import com.rentalplatform.model.vo.TokenPair;

/** 认证服务（技术方案 §5.2/§5.3）。 */
public interface AuthService {

    /** Web 账号密码登录：连续 5 次失败锁 15 分钟（20005） */
    LoginVO login(LoginDTO dto, String ip);

    /** 刷新令牌轮换（30 秒幂等宽限；宽限外重放清空全部会话） */
    TokenPair refresh(String refreshToken);

    /** 登出：access 拉黑 + refresh 删除 */
    void logout(String accessToken, String refreshToken);
}
