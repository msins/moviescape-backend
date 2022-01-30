package edu.fer.drumre.backend.user.dto;

import java.util.List;

public record RegistrationRequest(
    String fullName,
    String email,
    String authToken,
    List<String> favouriteGenres
) {

}
