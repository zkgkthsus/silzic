package com.ssafy.deathnotelive.error.exception;

public class RoomNotFoundException extends BusinessException {

    public RoomNotFoundException(String value) {
        super(value, ErrorCode.ROOM_NOT_FOUND);
    }

    public RoomNotFoundException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }
}
