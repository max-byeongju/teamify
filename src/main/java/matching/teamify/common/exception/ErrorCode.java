package matching.teamify.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 BAD_REQUEST
    RECRUITMENT_CLOSED(HttpStatus.BAD_REQUEST, "이미 마감된 모집입니다."),
    CANNOT_APPLY_TO_OWN_RECRUITMENT(HttpStatus.BAD_REQUEST, "자신이 등록한 모집 공고에는 지원할 수 없습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "지원할 수 없는 역할입니다."),
    INVALID_APPLICATION(HttpStatus.BAD_REQUEST, "지원 정보를 찾을 수 없습니다."),
    EMPTY_IMAGE_FILE(HttpStatus.BAD_REQUEST, "이미지 파일이 비어있습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 확장자입니다."),

    // 401 UNAUTHORIZED
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),

    // 403 FORBIDDEN
    NO_AUTHORIZATION(HttpStatus.FORBIDDEN, "요청에 대한 권한이 없습니다."),

    // 404 NOT_FOUND
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "지원 정보를 찾을 수 없습니다."),

    // 409 CONFLICT
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 사용중인 아이디입니다."),
    APPLICATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 지원한 내역이 존재합니다."),
    RECRUITMENT_FULL(HttpStatus.CONFLICT, "모집 인원이 모두 찼습니다."),
    DATA_CONFLICT(HttpStatus.CONFLICT, "데이터 충돌이 발생했습니다."),
    APPLICATION_ALREADY_APPROVED(HttpStatus.CONFLICT, "이미 승인된 지원입니다."),
    APPLICATION_ALREADY_REJECTED(HttpStatus.CONFLICT, "이미 거절된 지원입니다."),

    // 500 INTERNAL_SERVER_ERROR        // cancelApply 역할이 잘못 설정되어 있습니다.
    UNEXPECTED_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
