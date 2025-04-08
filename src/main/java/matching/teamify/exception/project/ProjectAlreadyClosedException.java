package matching.teamify.exception.project;

public class ProjectAlreadyClosedException extends RuntimeException {
    public ProjectAlreadyClosedException(String message) {
        super(message);
    }
}
