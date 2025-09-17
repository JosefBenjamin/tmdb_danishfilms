package app.enums;

public enum ErrorType {
    NOT_FOUND(404, "Resource not found"),
    ALREADY_EXISTS(409, "Resource already exists"),
    BAD_REQUEST(400, "Bad request"),
    CONFLICT(405, "Conflict"),
    DATABASE_ERROR(401, "Database error"),
    SERVER_ERROR(500, "Internal server error");

    private final int errorCode;
    private final String errorMessage;

    ErrorType(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMessage = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

}
