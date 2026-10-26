package com.rentalplatform.integration.wechat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.rentalplatform.common.exception.AuthException;
import com.rentalplatform.common.exception.BusinessException;
import com.rentalplatform.common.result.ResultCode;
import com.rentalplatform.config.RentalProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 微信服务端 API：code2session（技术方案 §5.2）。
 * 失败分支：code 无效 → 20007；网络/服务异常 → 90002（第三方暂不可用）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WxApiClient {

    private static final String JSCODE2SESSION =
            "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}"
                    + "&js_code={code}&grant_type=authorization_code";

    private final RentalProperties properties;

    /** 微信接口返回 JSON 内容却标 text/plain，RestClient 按 String 读再手动解析 */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public record WxSession(String openid, String unionid, String sessionKey) {
    }

    public WxSession code2Session(String code) {
        try {
            RestClient client = RestClient.create();
            String raw = client.get()
                    .uri(JSCODE2SESSION, properties.getWechat().getAppid(),
                            properties.getWechat().getSecret(), code)
                    .retrieve()
                    .body(String.class);
            JsonNode body = raw == null ? null : MAPPER.readTree(raw);
            if (body == null || body.hasNonNull("errcode") && body.get("errcode").asInt() != 0) {
                int errcode = body == null ? -1 : body.get("errcode").asInt(-1);
                String errmsg = body == null ? "empty" : body.path("errmsg").asText("");
                log.warn("jscode2session 失败 errcode={} errmsg={}", errcode, errmsg);
                // 40029 invalid code / 40163 code been used 等，统一映射为凭证无效
                throw new AuthException(ResultCode.WX_CODE_INVALID,
                        "微信登录凭证无效（errcode=" + errcode + "），请重试");
            }
            return new WxSession(
                    body.path("openid").asText(null),
                    body.path("unionid").asText(null),
                    body.path("session_key").asText(null));
        } catch (BusinessException e) {
            // AuthException 是其子类，业务异常原样上抛（20007 等）
            throw e;
        } catch (Exception e) {
            log.error("jscode2session 调用异常", e);
            throw new BusinessException(ResultCode.THIRD_PARTY_UNAVAILABLE);
        }
    }
}
