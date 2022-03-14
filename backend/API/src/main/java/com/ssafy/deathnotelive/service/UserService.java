package com.ssafy.deathnotelive.service;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.ssafy.deathnotelive.config.jwt.JwtUtils;
import com.ssafy.deathnotelive.dto.UserDto;
import com.ssafy.deathnotelive.entity.User;
import com.ssafy.deathnotelive.error.exception.EmailDuplicationException;
import com.ssafy.deathnotelive.error.exception.UserIdDuplicationException;
import com.ssafy.deathnotelive.error.exception.UserNotFoundException;
import com.ssafy.deathnotelive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //일반 유저 등록(관리자는 DB에서 직접 등록)
    public User signup(UserDto.UserRegist registerInfo) {
        String userId = registerInfo.getUserId();
        String password = registerInfo.getPassword();
        String email = registerInfo.getEmail();

        if (userRepository.getByUserId(userId) != null) {
            throw new UserIdDuplicationException("ERROR");
        } else if (userRepository.getByEmail(email) != null) {
            throw new EmailDuplicationException("ERROR");
        }
        return userRepository.save(User.builder()
                .userId(userId)
                .email(email)
                .password(passwordEncoder.encode(password))
                .authority("ROLE_USER")
                .build());
    }

    //유저 아이디로 찾기
    public User getByUserId(String userId) {
        User user = userRepository.getByUserId(userId);
        if (user == null) throw new UserNotFoundException("Error");
        else {
            return user;
        }
    }

    //로그인
    public String logIn(String userId, String password) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("Error"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            return JwtUtils.createToken(user);
        }
        throw new UserNotFoundException("Error");
    }

    //유저 정보 가져오기
    public UserDto.UserInfo getUserInfo(String userId) {
        User user = userRepository.getByUserId(userId);
        return UserDto.UserInfo.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .build();
    }

    //유저 정보 수정
    public void modifyUser(UserDto.ModifyUserInfo modifyUserInfo) {
        User user = userRepository.findByUserId(modifyUserInfo.getUserId()).orElseThrow(() -> new UserNotFoundException("EORROR"));
        user.setUserId(modifyUserInfo.getUserId());
        user.setEmail(modifyUserInfo.getEmail());
        userRepository.save(user);
    }

    //회원 탈퇴
    public void deleteUser(String userId) {
        User user = userRepository.getByUserId(userId);
        userRepository.delete(user);
    }
}
