package com.feng.demo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类（HS256 算法）
 * <p>
 * 结构：header.payload.signature，三部分均为 Base64URL 编码
 * - header:  {"alg":"HS256"}
 * - payload: {"id":员工ID, "username":用户名, "exp":过期时间戳(秒)}
 * - signature: HMACSHA256(header + "." + payload, secret)
 */
@Component
public class JwtUtils {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private final String secret;
    private final long expireSeconds;

    public JwtUtils(ObjectMapper objectMapper,
                    @Value("${jwt.secret}") String secret,
                    @Value("${jwt.expire-hours}") long expireHours) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.expireSeconds = expireHours * 3600;
    }

    /**
     * 生成令牌，携带员工 ID、用户名与过期时间
     */
    public String generateToken(Integer id, String username) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");

            Map<String, Object> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("username", username);
            payload.put("exp", System.currentTimeMillis() / 1000 + expireSeconds);

            String headerPart = base64Url(objectMapper.writeValueAsBytes(header));
            String payloadPart = base64Url(objectMapper.writeValueAsBytes(payload));
            String signaturePart = base64Url(hmacSha256(headerPart + "." + payloadPart));
            return headerPart + "." + payloadPart + "." + signaturePart;
        } catch (Exception e) {
            throw new IllegalStateException("生成令牌失败", e);
        }
    }

    /**
     * 校验并解析令牌，失败（签名不匹配 / 已过期 / 格式错误）返回 null
     */
    public Map<String, Object> parseToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            String expected = base64Url(hmacSha256(parts[0] + "." + parts[1]));
            if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8))) {
                return null;
            }
            JsonNode payload = objectMapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
            if (payload.path("exp").asLong() < System.currentTimeMillis() / 1000) {
                return null;
            }
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", payload.path("id").asInt());
            claims.put("username", payload.path("username").asText());
            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] hmacSha256(String data) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
