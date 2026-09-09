package ee.asya.veebipoodbackend.exception;

public record ErrorResponse(int status, String error, String message, String path) {
}
