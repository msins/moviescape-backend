package edu.fer.drumre.backend.user.dto;

public record LoginRequest(
    String email,
    String authToken
) {

}
