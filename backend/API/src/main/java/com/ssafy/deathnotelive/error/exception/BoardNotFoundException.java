package com.ssafy.deathnotelive.error.exception;

/**
 * 유저를 찾을 수 없을 때 발생하는 Exception
 */

public class BoardNotFoundException extends BusinessException {

    public BoardNotFoundException(String value) {
        super(value, ErrorCode.BOARD_NOT_FOUND);
    }

    public BoardNotFoundException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }
}
