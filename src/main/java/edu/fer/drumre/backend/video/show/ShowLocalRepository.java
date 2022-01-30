package edu.fer.drumre.backend.video.show;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowLocalRepository extends JpaRepository<Show, Long> {

  Optional<Show> findByUuid(UUID uuid);

  boolean existsByTitleAndReleaseDate(String title, LocalDate releaseDate);
}
