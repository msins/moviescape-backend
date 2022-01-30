package edu.fer.drumre.backend.video.popular;

import edu.fer.drumre.backend.video.Video;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PopularTodayGlobally {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne
  private Video video;

  @Column
  private long clicks;

  public PopularTodayGlobally() {
  }

  public PopularTodayGlobally(Video video, long clicks) {
    this.video = video;
    this.clicks = clicks;
  }

  public Long getId() {
    return id;
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

  public void setClicks(int clicks) {
    this.clicks = clicks;
  }
}
