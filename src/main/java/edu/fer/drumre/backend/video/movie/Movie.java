package edu.fer.drumre.backend.video.movie;

import edu.fer.drumre.backend.video.Video;
import java.util.StringJoiner;
import javax.persistence.Entity;

@Entity
public final class Movie extends Video {

  @Override
  public String toString() {
    return new StringJoiner(", ", Movie.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("uuid=" + uuid)
        .add("title='" + title + "'")
        .add("description=" + description)
        .toString();
  }
}
