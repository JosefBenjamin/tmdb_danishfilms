package app.exceptions;

public class MovieException extends ApiException {

    public MovieException(int code, String msg) {
        super(code, msg);
    }

    // Common Movie-specific exceptions
    public static MovieException notFound(int movieId) {
        return new MovieException(404, "Movie with ID " + movieId + " not found");
    }

    public static MovieException notFoundByTitle(String title) {
        return new MovieException(404, "Movie with title '" + title + "' not found");
    }

    public static MovieException alreadyExists(String title) {
        return new MovieException(409, "Movie with title '" + title + "' already exists");
    }

    public static MovieException invalidTitle(String title) {
        return new MovieException(400, "Invalid movie title: '" + title + "'. Title cannot be null or empty");
    }

    public static MovieException invalidReleaseYear(int year) {
        return new MovieException(400, "Invalid release year: " + year + ". Year must be between 1888 and " + (java.time.Year.now().getValue() + 10));
    }

    public static MovieException invalidRating(double rating) {
        return new MovieException(400, "Invalid movie rating: " + rating + ". Rating must be between 0.0 and 10.0");
    }

    public static MovieException databaseError(String operation) {
        return new MovieException(500, "Database error while performing movie " + operation);
    }

    public static MovieException noDirectorAssigned(int movieId) {
        return new MovieException(404, "No director assigned to movie with ID " + movieId);
    }

    public static MovieException noActorsAssigned(int movieId) {
        return new MovieException(404, "No actors found for movie with ID " + movieId);
    }

    public static MovieException noDirectorAssigned(Integer movieId) {
        return new MovieException(404, "No director assigned to movie with ID " + movieId);
    }

    public static MovieException noActorsAssigned(Integer movieId) {
        return new MovieException(404, "No actors found for movie with ID " + movieId);
    }
}
