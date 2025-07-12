package matching.teamify.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorTemplate {

    @JsonProperty("response_message")
    private final String message;

    public ErrorTemplate(String message) {
        this.message = message;
    }

    public static ErrorTemplate of(String message) {
        return new ErrorTemplate(message);
    }
}
