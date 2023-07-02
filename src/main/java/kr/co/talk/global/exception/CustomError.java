package kr.co.talk.global.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum CustomError {
    //
    USER_DOES_NOT_EXIST(1035, "해당 사용자가 존재하지 않습니다.", NOT_FOUND),

    //날짜 파라미터
    WRONG_DATE_FORMAT(3002, "날짜 형식이 잘못되었습니다.", BAD_REQUEST),

    // 공통
    SERVER_ERROR(3000, "알수 없는 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    FEIGN_ERROR(3001, "다른 API 호출에 실패하였습니다.", HttpStatus.BAD_GATEWAY.value()),
	KAFKA_ERROR(3003, "이벤트 발행에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());

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
