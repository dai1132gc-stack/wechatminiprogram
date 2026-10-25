package com.rentalplatform.security;

import com.rentalplatform.common.exception.PermissionException;
import com.rentalplatform.common.result.ResultCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** 从 SecurityContext 取当前登录人。 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static LoginUser getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser;
        }
        throw new PermissionException(ResultCode.UNAUTHORIZED);
    }

    public static long getUserId() {
        return getLoginUser().getUserId();
    }

    public static String getRole() {
        return getLoginUser().getRole();
    }
}
