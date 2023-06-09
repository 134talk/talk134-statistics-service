package kr.co.talk.global.exception;

import org.springframework.http.HttpStatus;

public enum CustomError {

    // 공통
    SERVER_ERROR(3000, "알수 없는 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    FEIGN_ERROR(3001, "다른 API 호출에 실패하였습니다.", HttpStatus.BAD_GATEWAY.value());

    private int errorCode;
    private String message;
    private int statusCode;

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }


    CustomError(int errorCode, String message, int statusCode) {
        this.errorCode = errorCode;
        this.message = message;
        this.statusCode = statusCode;
    }
}
