package edu.fer.drumre.backend.video.shared;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

  @Query("SELECT genre FROM Genre genre"
      + " LEFT JOIN FETCH genre.videos video"
      + " WHERE genre.name =:name")
  Optional<Genre> findByNameEager(String name);
}
