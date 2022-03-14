package com.ssafy.deathnotelive.error.exception;

public class ParticipantsNotFoundException extends BusinessException {

    public ParticipantsNotFoundException(String value) {
        super(value, ErrorCode.ROOM_NOT_FOUND);
    }

    public ParticipantsNotFoundException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }
}
