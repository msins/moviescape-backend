package edu.fer.drumre.backend.video.analytics;

import edu.fer.drumre.backend.video.Video;

public class VideoPopularity {

  private final Video video;
  private final long clicks;

  public VideoPopularity(Video video, long clicks) {
    this.video = video;
    this.clicks = clicks;
  }

  public Video getVideo() {
    return video;
  }

  public long getClicks() {
    return clicks;
  }
}
