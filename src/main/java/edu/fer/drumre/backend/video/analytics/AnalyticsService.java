package edu.fer.drumre.backend.video.analytics;

import edu.fer.drumre.backend.location.Country;
import edu.fer.drumre.backend.video.Video;
import io.reactivex.rxjava3.core.Single;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {

  private final VideoClickedRepository videoClickedRepository;

  @Autowired
  public AnalyticsService(VideoClickedRepository videoClickedRepository) {
    this.videoClickedRepository = videoClickedRepository;
  }

  @Transactional
  public Single<VideoClicked> videoClicked(Video video, @Nullable Country country) {
    return Single.create(subscriber -> {
      var newVideoClick = new VideoClicked();

      newVideoClick.setVideo(video);
      newVideoClick.setTime(LocalDateTime.now());
      if (country != null) {
        newVideoClick.setCountry(country);
      }

      subscriber.onSuccess(videoClickedRepository.save(newVideoClick));
    });
  }
}
