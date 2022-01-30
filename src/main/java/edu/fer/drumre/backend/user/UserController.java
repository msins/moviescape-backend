package edu.fer.drumre.backend.user;

import edu.fer.drumre.backend.user.dto.LoginRequest;
import edu.fer.drumre.backend.user.dto.RegistrationRequest;
import edu.fer.drumre.backend.user.dto.UserNameResponse;
import io.reactivex.rxjava3.core.Single;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  // not using any authentication (except facebook token) for simplicity
  @PostMapping("register")
  public Single<ResponseEntity<Void>> register(
      @RequestBody RegistrationRequest request
  ) {
    return userService.register(request)
        .map(user -> ResponseEntity.ok().build());
  }

  // not using any authentication (except facebook token) for simplicity
  @PostMapping("login")
  public Single<ResponseEntity<Void>> login(
      @RequestBody LoginRequest request
  ) {
    return userService.login(request)
        .map(user -> ResponseEntity.ok().build());
  }

  @GetMapping("name")
  public Single<ResponseEntity<UserNameResponse>> getUserNameFromToken(
      @RequestHeader("Authorization") String authToken
  ) {
    return userService.getUserByToken(authToken)
        .map(user -> new UserNameResponse(user.getFullName()))
        .map(ResponseEntity::ok);
  }
}
