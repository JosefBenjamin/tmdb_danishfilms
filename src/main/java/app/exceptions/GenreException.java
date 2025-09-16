package app.exceptions;

public class GenreException extends ApiException {

    public GenreException(int code, String msg) {
        super(code, msg);
    }

    // Common Genre-specific exceptions
    public static GenreException notFound(int genreId) {
        return new GenreException(404, "Genre with ID " + genreId + " not found");
    }

    public static GenreException notFoundByName(String genreName) {
        return new GenreException(404, "Genre with name '" + genreName + "' not found");
    }

    public static GenreException alreadyExists(String genreName) {
        return new GenreException(409, "Genre with name '" + genreName + "' already exists");
    }

    public static GenreException invalidName(String name) {
        return new GenreException(400, "Invalid genre name: '" + name + "'. Name cannot be null or empty");
    }

    public static GenreException databaseError(String operation) {
        return new GenreException(500, "Database error while performing genre " + operation);
    }

    public static GenreException hasAssociatedMovies(int genreId) {
        return new GenreException(409, "Cannot delete genre with ID " + genreId + " because it has associated movies");
    }
}
