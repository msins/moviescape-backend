package edu.fer.drumre.backend.video.show;


import edu.fer.drumre.backend.video.Video;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public final class Show extends Video {

  @Column
  private int numOfEpisodes;

  @Column
  private int numOfSeasons;

  public int getNumOfEpisodes() {
    return numOfEpisodes;
  }

  public void setNumOfEpisodes(int numOfEpisodes) {
    this.numOfEpisodes = numOfEpisodes;
  }

  public int getNumOfSeasons() {
    return numOfSeasons;
  }

  public void setNumOfSeasons(int numOfSeasons) {
    this.numOfSeasons = numOfSeasons;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Show.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("uuid=" + uuid)
        .add("title='" + title + "'")
        .add("description='" + description + "'")
        .add("numOfEpisodes=" + numOfEpisodes)
        .add("numOfSeasons=" + numOfSeasons)
        .toString();
  }
}
