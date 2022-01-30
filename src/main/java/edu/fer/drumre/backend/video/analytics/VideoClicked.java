package edu.fer.drumre.backend.video.analytics;

import edu.fer.drumre.backend.location.Country;
import edu.fer.drumre.backend.video.Video;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class VideoClicked {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne
  private Video video;

  @ManyToOne
  private Country country;

  @Column
  private LocalDateTime time;

  public Long getId() {
    return id;
  }

  public Video getVideo() {
    return video;
  }

  public void setVideo(Video video) {
    this.video = video;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  public LocalDateTime getTime() {
    return time;
  }
}
