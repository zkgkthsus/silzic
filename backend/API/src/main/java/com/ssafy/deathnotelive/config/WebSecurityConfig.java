package com.ssafy.deathnotelive.config;

import com.ssafy.deathnotelive.config.jwt.JwtAuthenticationFilter;
import com.ssafy.deathnotelive.config.jwt.JwtAuthorizationFilter;
import com.ssafy.deathnotelive.config.jwt.JwtUtils;
import com.ssafy.deathnotelive.entity.User;
import com.ssafy.deathnotelive.repository.UserRepository;
import com.ssafy.deathnotelive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security 설정 Config
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // basic authentication
        http
                .cors().and()
                .httpBasic().disable() // basic authentication filter 비활성화
                .csrf().disable() //csrf 안씀
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); //토큰 기반 인증(세션 X)

        // jwt filter
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtUtils),
                UsernamePasswordAuthenticationFilter.class
        ).addFilterBefore(
                new JwtAuthorizationFilter(userRepository),
                BasicAuthenticationFilter.class
        );

        // authorization
        http.authorizeRequests()
                // /와 /home, /room의 참가, 닉네임 중복확인은 모두에게 허용
                .antMatchers("/", "/home", "/user/signup", "/user/login", "/room/nickName", "/room/join/{userId}", "/room/finish/{roomCode}", "/room/delete/{sessionId}").permitAll()
                .antMatchers(HttpMethod.POST, "/notice/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/notice/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/notice/**").hasRole("ADMIN")
                .anyRequest().authenticated();

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 정적 리소스 spring security 대상에서 제외
        web.ignoring()
                .antMatchers("/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8081"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Access-Control-Allow-Credentials"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
