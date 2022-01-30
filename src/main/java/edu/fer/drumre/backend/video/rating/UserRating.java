package edu.fer.drumre.backend.video.rating;

import edu.fer.drumre.backend.user.User;
import edu.fer.drumre.backend.video.Video;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
public class UserRating {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne
  private Video video;

  @ManyToOne
  private User user;

  @Min(value = 0, message = "Rating can't be lower than 0")
  @Max(value = 5, message = "Rating can't be higher than 5")
  @Column(nullable = false)
  private int rating;

  public Long getId() {
    return id;
  }

  public void setVideo(Video video) {
    this.video = video;
  }

  public Video getVideo() {
    return video;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public int getRating() {
    return rating;
  }
}