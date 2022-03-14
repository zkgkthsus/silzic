package com.ssafy.deathnotelive.error.exception;

import lombok.NoArgsConstructor;

/**
 * 유저를 찾을 수 없을 때 발생하는 Exception
 */

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String value) {
        super(value, ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }
}
