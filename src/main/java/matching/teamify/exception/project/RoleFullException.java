package matching.teamify.exception.project;

import lombok.Getter;
import matching.teamify.domain.ProjectRole;

@Getter
public class RoleFullException extends RuntimeException {

    private final ProjectRole role;

    public RoleFullException(ProjectRole role, String message) {
        super(message);
        this.role = role;
    }
}
