package app.exceptions;

public class DirectorException extends ApiException {

    public DirectorException(int code, String msg) {
        super(code, msg);
    }

    // Common Director-specific exceptions
    public static DirectorException notFound(int directorId) {
        return new DirectorException(404, "Director with ID " + directorId + " not found");
    }

    public static DirectorException alreadyExists(String directorName) {
        return new DirectorException(409, "Director with name '" + directorName + "' already exists");
    }

    public static DirectorException invalidAge(int age) {
        return new DirectorException(400, "Invalid director age: " + age + ". Age must be between 0 and 150");
    }

    public static DirectorException invalidName(String name) {
        return new DirectorException(400, "Invalid director name: '" + name + "'. Name cannot be null or empty");
    }

    public static DirectorException databaseError(String operation) {
        return new DirectorException(500, "Database error while performing director " + operation);
    }

    public static DirectorException noMoviesDirected(int directorId) {
        return new DirectorException(404, "No movies found for director with ID " + directorId);
    }
}
