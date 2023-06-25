package kr.co.talk.global.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum CustomError {
    //
    USER_DOES_NOT_EXIST(1035, "해당 사용자가 존재하지 않습니다.", NOT_FOUND),

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

    CustomError(int errorCode, String message, HttpStatus status) {
        this.errorCode = errorCode;
        this.message = message;
        this.statusCode = status.value();
    }
}
