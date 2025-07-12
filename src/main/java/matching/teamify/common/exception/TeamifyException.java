package matching.teamify.common.exception;

import lombok.Getter;

@Getter
public class TeamifyException extends RuntimeException {

    private final ErrorCode errorCode;

    public TeamifyException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public TeamifyException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
