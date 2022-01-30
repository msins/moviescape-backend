package edu.fer.drumre.backend.video.recommendations;

import edu.fer.drumre.backend.user.User;
import edu.fer.drumre.backend.video.Video;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class RecommendationForUser {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne
  private User user;

  @ManyToOne
  private Video video;

  @Column
  private double score;

  public RecommendationForUser(User user, Video video, double score) {
    this.user = user;
    this.video = video;
    this.score = score;
  }

  public RecommendationForUser() {
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Video getVideo() {
    return video;
  }

  public void setVideo(Video video) {
    this.video = video;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }
}
