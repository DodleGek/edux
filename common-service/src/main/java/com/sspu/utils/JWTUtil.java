package com.sspu.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {


    private static final String SECRET = "ldjfklajsfjas";
    public static final int EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    /**
     * 生成Token
     * @param map 存储在token中的信息
     * @return token字符串
     */
    public static String getToken(Map<String, String> map) {
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
            JWTCreator.Builder builder = JWT.create();

            // payload
            map.forEach((key, value) -> {
                if (value != null) {  // 添加空值检查
                    builder.withClaim(key, value);
                }
            });

            return builder.withExpiresAt(expiresAt)
                    .sign(Algorithm.HMAC256(SECRET));
        } catch (Exception e) {
            throw new RuntimeException("Token生成失败", e);
        }
    }

    /**
     * 验证token的合法性
     * @param token JWT token
     * @return 是否有效
     */
    public static boolean verify(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            JWT.require(Algorithm.HMAC256(SECRET))
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取token的信息
     * @param token JWT token
     * @return 解码后的JWT对象
     * @throws JWTVerificationException 当token无效时抛出异常
     */
    public static DecodedJWT getTokenInfo(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token不能为空");
        }
        try {
            return JWT.require(Algorithm.HMAC256(SECRET))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Token验证失败", e);
        }
    }

    /**
     * 从token中获取指定的claim
     * @param token JWT token
     * @param claim claim名称
     * @return claim的值
     */
    public static String getClaimFromToken(String token, String claim) {
        try {
            DecodedJWT jwt = getTokenInfo(token);
            return jwt.getClaim(claim).asString();
        } catch (Exception e) {
            return null;
        }
    }
}
