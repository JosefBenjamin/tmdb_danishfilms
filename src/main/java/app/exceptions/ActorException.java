package app.exceptions;

public class ActorException extends ApiException {

    public ActorException(int code, String msg) {
        super(code, msg);
    }

    // Common Actor-specific exceptions
    public static ActorException notFound(int actorId) {
        return new ActorException(404, "Actor with ID " + actorId + " not found");
    }

    public static ActorException alreadyExists(String actorName) {
        return new ActorException(409, "Actor with name '" + actorName + "' already exists");
    }

    public static ActorException invalidAge(int age) {
        return new ActorException(400, "Invalid actor age: " + age + ". Age must be between 0 and 150");
    }

    public static ActorException invalidName(String name) {
        return new ActorException(400, "Invalid actor name: '" + name + "'. Name cannot be null or empty");
    }

    public static ActorException databaseError(String operation) {
        return new ActorException(500, "Database error while performing actor " + operation);
    }
}
