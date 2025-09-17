package app.exceptions;

import app.enums.ErrorType;


public class ApiException extends RuntimeException {
    private final int code;
    private final ErrorType errorType;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        this.errorType = null;
    }

    public int getCode() {
        return code;
    }

    public ApiException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.code = errorType.getErrorCode();
    }

    //TODO: 400 bad request
    public static ApiException badRequest (String msg) {
        return new ApiException(ErrorType.BAD_REQUEST, ErrorType.BAD_REQUEST.getErrorMessage() + "\n" + msg);
    }

    //TODO: 404 not found
    public static ApiException notFound(String msg) {
        return new ApiException(ErrorType.NOT_FOUND, ErrorType.NOT_FOUND.getErrorMessage() + "\n" + msg);

    }


    //TODO: 409 already exists
    public static ApiException alreadyExists(String msg) {
        return new ApiException(ErrorType.ALREADY_EXISTS, ErrorType.ALREADY_EXISTS.getErrorMessage() + "\n" + msg);

    }


    //TODO: 405 conflict
    public static ApiException conflict (String msg) {
        return new ApiException(ErrorType.CONFLICT, ErrorType.CONFLICT.getErrorMessage() + "\n" + msg);

    }

    //TODO: 500 server error
    public static ApiException serverError (String msg) {
        return new ApiException(ErrorType.SERVER_ERROR, ErrorType.SERVER_ERROR.getErrorMessage() + "\n" + msg);
    }

}

