package matching.teamify.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TeamifyException.class)
    public ResponseEntity<ErrorTemplate> handleTeamifyException(TeamifyException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("Teamify Exception Occurred: {}", errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorTemplate.of(errorCode.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorTemplate> handleAllUncaughtException(Exception ex) {
        log.error("Uncaught Exception Occurred", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorTemplate.of("서버 내부 오류가 발생했습니다."));
    }
}
