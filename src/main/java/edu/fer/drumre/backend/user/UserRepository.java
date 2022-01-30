package edu.fer.drumre.backend.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByAuthToken(String authToken);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
