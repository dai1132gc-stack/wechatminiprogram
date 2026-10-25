package com.rentalplatform.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalplatform.common.constant.RedisKey;
import com.rentalplatform.common.exception.AuthException;
import com.rentalplatform.common.result.ResultCode;
import com.rentalplatform.model.vo.TokenPair;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

/**
 * 双令牌生命周期（技术方案 §5.1/§5.3）：
 * - refresh 一次性轮换：旧 jti 删除前写入 used 集，30 秒宽限期内重放返回同一新令牌对（幂等）；
 * - 宽限期外的重放视为令牌被盗：清空该用户全部 refresh，强制重新登录（20003）；
 * - 登出：access jti 进黑名单（TTL=剩余有效期）。
 * 并发刷新的坑（5 个请求同时 401）由前端刷新队列 + 本宽限期共同兜底。
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    /** 30 秒幂等宽限窗口 */
    static final Duration GRACE = Duration.ofSeconds(30);

    private final JwtTokenProvider provider;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public record IssuedTokens(String accessToken, String refreshToken, long expiresIn) {
    }

    /** 登录成功后签发双令牌，refresh 写入 Redis 白名单 */
    @SneakyThrows
    public IssuedTokens issue(long userId, String role, int tokenVersion, String deviceId) {
        String access = provider.issueAccessToken(userId, role, tokenVersion);
        String refresh = provider.issueRefreshToken(userId, role, tokenVersion, deviceId);
        var claims = provider.parse(refresh);
        String whitelistValue = objectMapper.writeValueAsString(
                java.util.Map.of("deviceId", deviceId, "expireAt", claims.expiresAt().getTime()));
        redis.opsForValue().set(
                RedisKey.REFRESH + userId + ":" + claims.jti(),
                whitelistValue,
                Duration.ofSeconds(provider.refreshTtlSeconds()));
        return new IssuedTokens(access, refresh, provider.accessTtlSeconds());
    }

    /**
     * 刷新轮换。返回的新对在宽限期内对同一旧令牌幂等。
     * 旧 jti 不在白名单且不在 used 集 → 判定重放，清空全部会话。
     */
    @SneakyThrows
    public TokenPair rotate(String oldRefreshToken) {
        var claims = provider.parse(oldRefreshToken);
        if (!JwtTokenProvider.TYPE_REFRESH.equals(claims.type())) {
            throw new AuthException(ResultCode.REFRESH_INVALID);
        }
        String whitelistKey = RedisKey.REFRESH + claims.userId() + ":" + claims.jti();
        String usedKey = RedisKey.REFRESH_USED + claims.jti();

        // ① 宽限期内重放：返回同一新令牌对（并发刷新场景）
        String usedValue = redis.opsForValue().get(usedKey);
        if (usedValue != null) {
            return objectMapper.readValue(usedValue, TokenPair.class);
        }

        // ② 白名单不存在 → 已使用过且超宽限，或伪造：视为被盗，清空全部 refresh
        if (Boolean.FALSE.equals(redis.hasKey(whitelistKey))) {
            revokeAll(claims.userId());
            throw new AuthException(ResultCode.REFRESH_INVALID);
        }

        // ③ 正常轮换：删旧 jti → 签发新对 → 旧 jti 写入 used 集（带 TokenPair，供幂等返回）
        redis.delete(whitelistKey);
        IssuedTokens issued = reissueForRotation(claims);
        TokenPair pair = new TokenPair(issued.accessToken(), issued.refreshToken(), issued.expiresIn());
        redis.opsForValue().set(usedKey, objectMapper.writeValueAsString(pair), GRACE);
        return pair;
    }

    /** 轮换时 ver 沿用旧令牌（版本变化由改密/禁用流程负责，刷新不应改变版本语义） */
    private IssuedTokens reissueForRotation(JwtTokenProvider.TokenClaims old) {
        String access = provider.issueAccessToken(old.userId(), old.role(), old.tokenVersion());
        String refresh = provider.issueRefreshToken(old.userId(), old.role(), old.tokenVersion(), "rotate");
        var claims = provider.parse(refresh);
        redis.opsForValue().set(
                RedisKey.REFRESH + old.userId() + ":" + claims.jti(),
                "{\"deviceId\":\"rotate\"}",
                Duration.ofSeconds(provider.refreshTtlSeconds()));
        return new IssuedTokens(access, refresh, provider.accessTtlSeconds());
    }

    /** 登出：access 拉黑（TTL=剩余有效期），refresh 删除 */
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            try {
                var claims = provider.parse(accessToken);
                long remain = claims.expiresAt().getTime() - System.currentTimeMillis();
                if (remain > 0) {
                    redis.opsForValue().set(
                            RedisKey.BLACKLIST + claims.jti(), "1", Duration.ofMillis(remain));
                }
            } catch (Exception ignored) {
                // 已过期/非法的 access 无需拉黑
            }
        }
        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                var claims = provider.parse(refreshToken);
                redis.delete(RedisKey.REFRESH + claims.userId() + ":" + claims.jti());
            } catch (Exception ignored) {
            }
        }
    }

    /** access 是否已被拉黑 */
    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redis.hasKey(RedisKey.BLACKLIST + jti));
    }

    /** 改密/禁用/改角色时：清空该用户全部 refresh 会话 */
    public void revokeAll(long userId) {
        Set<String> keys = redis.keys(RedisKey.REFRESH + userId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redis.delete(keys);
        }
    }
}
