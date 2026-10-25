package com.rentalplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rentalplatform.common.constant.RedisKey;
import com.rentalplatform.common.exception.AuthException;
import com.rentalplatform.common.result.ResultCode;
import com.rentalplatform.mapper.UserMapper;
import com.rentalplatform.model.dto.LoginDTO;
import com.rentalplatform.model.entity.SysUser;
import com.rentalplatform.model.vo.LoginVO;
import com.rentalplatform.model.vo.TokenPair;
import com.rentalplatform.model.vo.UserInfo;
import com.rentalplatform.security.LoginUser;
import com.rentalplatform.security.TokenService;
import com.rentalplatform.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /** 连续失败 5 次锁定 15 分钟 */
    private static final int MAX_FAIL = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private static final Map<String, String[]> ROLE_PERMISSIONS = Map.of(
            "ADMIN", new String[]{"*"},
            "LANDLORD", new String[]{"house:create", "house:update", "application:approve",
                    "device:bind", "alarm:handle"},
            "TENANT", new String[]{"appointment:create", "application:create", "contract:sign"});

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final StringRedisTemplate redis;

    @Override
    public LoginVO login(LoginDTO dto, String ip) {
        String failKey = RedisKey.LOGIN_FAIL + dto.getUsername();
        String fails = redis.opsForValue().get(failKey);
        if (fails != null && Integer.parseInt(fails) >= MAX_FAIL) {
            throw new AuthException(ResultCode.LOGIN_TOO_FREQUENT);
        }

        LoginUser loginUser;
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
            loginUser = (LoginUser) authentication.getPrincipal();
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            recordFailure(failKey);
            throw new AuthException(ResultCode.USERNAME_PASSWORD_ERROR);
        } catch (DisabledException e) {
            throw new AuthException(ResultCode.ACCOUNT_DISABLED);
        }

        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            throw new AuthException(ResultCode.ACCOUNT_DISABLED);
        }

        // 更新最后登录信息（nickname 等以 DB 为准）
        redis.delete(failKey);
        var issued = tokenService.issue(user.getId(), user.getRole(),
                user.getTokenVersion(), "web-" + ip);

        return LoginVO.builder()
                .accessToken(issued.accessToken())
                .refreshToken(issued.refreshToken())
                .expiresIn(issued.expiresIn())
                .userInfo(UserInfo.from(user))
                .permissions(ROLE_PERMISSIONS.getOrDefault(user.getRole(), new String[]{}))
                .build();
    }

    @Override
    public TokenPair refresh(String refreshToken) {
        try {
            return tokenService.rotate(refreshToken);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            log.warn("刷新令牌解析失败: {}", e.getMessage());
            throw new AuthException(ResultCode.REFRESH_INVALID);
        }
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        tokenService.logout(accessToken, refreshToken);
    }

    private void recordFailure(String failKey) {
        Long count = redis.opsForValue().increment(failKey);
        if (count != null && count == 1) {
            redis.expire(failKey, LOCK_DURATION);
        }
    }
}
