package edu.fer.drumre.backend.user;

import edu.fer.drumre.backend.core.exceptions.EntityWithIdNotFound;
import edu.fer.drumre.backend.user.dto.LoginRequest;
import edu.fer.drumre.backend.user.dto.RegistrationRequest;
import edu.fer.drumre.backend.user.exceptions.LoginFailed;
import edu.fer.drumre.backend.user.exceptions.RegistrationFailed;
import edu.fer.drumre.backend.video.shared.Genre;
import edu.fer.drumre.backend.video.shared.GenreRepository;
import io.reactivex.rxjava3.core.Single;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final GenreRepository genreRepository;

  @Autowired
  public UserService(UserRepository userRepository, GenreRepository genreRepository) {
    this.userRepository = userRepository;
    this.genreRepository = genreRepository;
  }

  @Transactional
  public Single<User> register(RegistrationRequest request) {
    return Single.create(subscriber -> {
      var userExists = userRepository.existsByEmail(request.email());
      if (userExists) {
        subscriber.onError(
            new RegistrationFailed("User with email " + request.email() + " already exists.")
        );
        return;
      }

      var newUser = new User();
      newUser.setEmail(request.email());
      newUser.setAuthToken(request.authToken());
      newUser.setFullName(request.fullName());

      var favouriteGenres = new HashSet<Genre>();
      for (var genreName : request.favouriteGenres()) {
        var genreOptional = genreRepository.findByNameEager(genreName);
        if (genreOptional.isEmpty()) {
          subscriber.onError(new RegistrationFailed("Invalid genre " + genreName));
          return;
        }
        var genre = genreOptional.get();
        favouriteGenres.add(genre);
      }

      newUser.setFavouriteGenres(favouriteGenres);
      subscriber.onSuccess(userRepository.save(newUser));
    });
  }

  // login is not secure, but this is a demo anyway
  @Transactional
  public Single<User> login(LoginRequest request) {
    return Single.create(subscriber -> {
      var userOptional = userRepository.findByEmail(request.email());
      if (userOptional.isEmpty()) {
        subscriber.onError(new LoginFailed("User with email " + request.email() + " doesn't exist."));
        return;
      }

      var user = userOptional.get();

      if (!user.getAuthToken().equals(request.authToken())) {
        user.setAuthToken(request.authToken());
        userRepository.saveAndFlush(user);
      }

      subscriber.onSuccess(user);
    });
  }

  @Transactional(readOnly = true)
  public Single<User> getUserByToken(String authToken) {
    return Single.create(subscriber -> {
      var userOptional = userRepository.findByAuthToken(authToken);
      if (userOptional.isEmpty()) {
        subscriber.onError(new EntityWithIdNotFound(User.class, authToken));
        return;
      }

      subscriber.onSuccess(userOptional.get());
    });
  }
}
