package com.ssafy.deathnotelive.error.exception;

/**
 * 이미 등록된 유저를 재등록하려고 할때 발생하는 Exception
 */
public class EmailDuplicationException extends BusinessException {

    public EmailDuplicationException(String value) {
        super(value, ErrorCode.EMAIL_DUPLICATION);
    }

    public EmailDuplicationException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }
}
