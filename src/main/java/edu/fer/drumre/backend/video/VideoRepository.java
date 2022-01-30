package edu.fer.drumre.backend.video;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {

  Optional<Video> findByUuid(UUID uuid);
}
