package matching.teamify.exception.project;

import lombok.Getter;
import matching.teamify.domain.ApplyStatus;

@Getter
public class InvalidApplicationStatusException extends RuntimeException {

    private final ApplyStatus status;

    public InvalidApplicationStatusException(ApplyStatus status, String message) {
      super(message);
      this.status = status;
    }
}