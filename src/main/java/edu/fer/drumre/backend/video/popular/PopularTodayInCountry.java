package edu.fer.drumre.backend.video.popular;

import edu.fer.drumre.backend.location.Country;
import edu.fer.drumre.backend.video.Video;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PopularTodayInCountry {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne
  private Country country;

  @ManyToOne
  private Video video;

  @Column
  private long clicks;

  public PopularTodayInCountry() {
  }

  public PopularTodayInCountry(Country country, Video video, long clicks) {
    this.country = country;
    this.video = video;
    this.clicks = clicks;
  }

  public Long getId() {
    return id;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public Video getVideo() {
    return video;
  }

  public void setVideo(Video video) {
    this.video = video;
  }

  public long getClicks() {
    return clicks;
  }

  public void setClicks(long clicks) {
    this.clicks = clicks;
  }
}
