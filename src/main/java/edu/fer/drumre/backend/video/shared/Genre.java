package edu.fer.drumre.backend.video.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.fer.drumre.backend.video.Video;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.NaturalId;

@Entity
public class Genre {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column
  @NaturalId
  private String name;

  @ManyToMany(mappedBy = "genres")
  private Set<Video> videos = new HashSet<>();

  public static Genre fromName(String name) {
    var newGenre = new Genre();
    newGenre.name = name;
    return newGenre;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Video> getVideos() {
    return videos;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Genre genre = (Genre) o;
    return name.equals(genre.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

}
