package edu.fer.drumre.backend.video;

import edu.fer.drumre.backend.video.shared.Genre;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.validator.constraints.URL;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "video_type", discriminatorType = DiscriminatorType.INTEGER)
public abstract class Video {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  protected Long id;

  /**
   *
   * Used only for apis to hide real id.
   */
  @Column(nullable = false, unique = true)
  protected UUID uuid;

  @Column(nullable = false)
  protected String title;

  @Column(nullable = false)
  protected LocalDate releaseDate;

  @URL
  @Column
  protected String coverUrl;

  @Column(nullable = false, columnDefinition = "LONGTEXT")
  protected String description;

  @ManyToMany
  @JoinTable(
      name = "video_genre",
      joinColumns = {@JoinColumn(name = "video_id")},
      inverseJoinColumns = {@JoinColumn(name = "genre_id")}
  )
  protected Set<Genre> genres = new HashSet<>();

  public void addGenre(Genre genre) {
    this.genres.add(genre);
    genre.getVideos().add(this);
  }

  public void removeGenre(Genre genre) {
    this.genres.remove(genre);
    genre.getVideos().remove(this);
  }

  public Long getId() {
    return id;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Set<Genre> getGenres() {
    return Set.copyOf(genres);
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getCoverUrl() {
    return coverUrl;
  }

  public void setCoverUrl(String coverUrl) {
    this.coverUrl = coverUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Video video = (Video) o;
    return uuid.equals(video.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }
}
