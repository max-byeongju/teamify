package matching.teamify.exception.common;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + "( ID: " + id + ") 를 찾을 수 없습니다.");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
