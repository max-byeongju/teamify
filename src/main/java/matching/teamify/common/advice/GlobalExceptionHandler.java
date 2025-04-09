package matching.teamify.common.advice;

import matching.teamify.exception.common.DataConflictException;
import matching.teamify.exception.common.ApplicationNotFoundException;
import matching.teamify.exception.auth.AuthenticationFailedException;
import matching.teamify.exception.common.EntityNotFoundException;
import matching.teamify.exception.project.InvalidApplicationStatusException;
import matching.teamify.exception.project.MyProjectApplyException;
import matching.teamify.exception.project.ProjectAlreadyClosedException;
import matching.teamify.exception.project.RoleFullException;
import matching.teamify.exception.study.MyStudyApplyException;
import matching.teamify.exception.study.StudyAlreadyClosedException;
import matching.teamify.exception.study.StudyFullException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectAlreadyClosedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorTemplate handleProjectAlreadyClosedException(ProjectAlreadyClosedException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }

    @ExceptionHandler(MyProjectApplyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorTemplate handleMyProjectApplyException(MyProjectApplyException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }

    @ExceptionHandler(RoleFullException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorTemplate handleRoleFullException(RoleFullException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }

    @ExceptionHandler(InvalidApplicationStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorTemplate handleInvalidApplicationStatusException(InvalidApplicationStatusException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }

    @ExceptionHandler(StudyAlreadyClosedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorTemplate handleStudyAlreadyClosedException(StudyAlreadyClosedException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }

    @ExceptionHandler(MyStudyApplyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorTemplate handleMyStudyApplyException(MyStudyApplyException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }

    @ExceptionHandler(StudyFullException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorTemplate handleStudyFullException(StudyFullException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorTemplate handleApplicationNotFoundException(ApplicationNotFoundException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorTemplate handleEntityNotFoundException(EntityNotFoundException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorTemplate handleAuthenticationFailedException(AuthenticationFailedException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }

    @ExceptionHandler(DataConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorTemplate handleDataConflictException(DataConflictException ex) {
        return ErrorTemplate.of(ex.getMessage());
    }
}
