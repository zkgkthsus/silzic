package com.ssafy.deathnotelive.error.exception;

import java.io.Serializable;

/**
 * 이미 등록된 유저를 재등록하려고 할때 발생하는 Exception
 */
public class UserIdDuplicationException extends BusinessException  {

    public UserIdDuplicationException(String value) {
        super(value, ErrorCode.USERID_DUPLICATION);
    }

    public UserIdDuplicationException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }
}
