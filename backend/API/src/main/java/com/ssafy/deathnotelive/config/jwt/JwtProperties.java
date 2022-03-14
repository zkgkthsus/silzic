package com.ssafy.deathnotelive.config.jwt;

/**
 * JWT 기본 설정값
 */
public class JwtProperties {
    public static final int EXPIRATION_TIME = 60 * 60 * 1000; // 60분 * 60초 * 1000ms
    public static final String COOKIE_NAME = "JWT-AUTHENTICATION";
}