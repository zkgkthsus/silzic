package com.ssafy.deathnotelive.error.exception;

/**
 * 유저를 찾을 수 없을 때 발생하는 Exception
 */

public class NoticeNotFoundException extends BusinessException {

    public NoticeNotFoundException(String value) {
        super(value, ErrorCode.NOTICE_NOT_FOUND);
    }

    public NoticeNotFoundException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }
}
