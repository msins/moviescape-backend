package edu.fer.drumre.backend.core.networking;

public record ApiError(
    String error,
    String message,
    int status
) {

}
