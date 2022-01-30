package edu.fer.drumre.backend.video.scheduling;

import edu.fer.drumre.backend.location.Country;
import edu.fer.drumre.backend.location.CountryRepository;
import edu.fer.drumre.backend.video.Video;
import edu.fer.drumre.backend.video.analytics.VideoClickedRepository;
import edu.fer.drumre.backend.video.movie.Movie;
import edu.fer.drumre.backend.video.popular.PopularTodayGlobally;
import edu.fer.drumre.backend.video.popular.PopularTodayGloballyRepository;
import edu.fer.drumre.backend.video.popular.PopularTodayInCountry;
import edu.fer.drumre.backend.video.popular.PopularTodayInCountryRepository;
import edu.fer.drumre.backend.video.show.Show;
import java.time.LocalDateTime;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VideoPopularityScheduler {

  private static final Logger logger = LoggerFactory.getLogger(VideoPopularityScheduler.class);

  private final VideoClickedRepository videoClickedRepository;
  private final PopularTodayGloballyRepository popularTodayGloballyRepository;
  private final PopularTodayInCountryRepository popularTodayInCountryRepository;
  private final CountryRepository countryRepository;

  @Autowired
  public VideoPopularityScheduler(
      VideoClickedRepository videoClickedRepository,
      PopularTodayGloballyRepository popularTodayGloballyRepository,
      PopularTodayInCountryRepository popularTodayInCountryRepository,
      CountryRepository countryRepository
  ) {
    this.videoClickedRepository = videoClickedRepository;
    this.popularTodayGloballyRepository = popularTodayGloballyRepository;
    this.popularTodayInCountryRepository = popularTodayInCountryRepository;
    this.countryRepository = countryRepository;
  }

  @Scheduled(cron = "0 0 * * * *")
  @Transactional
  void updatePopular() {
    logger.info("Updating globally popular videos.");
    popularTodayGloballyRepository.deleteAllInBatch();

    updateGloballyPopularToday(Movie.class);
    updateGloballyPopularToday(Show.class);

    logger.info("Finished updating globally popular videos.");
  }

  private void updateGloballyPopularToday(Class<? extends Video> ofType) {
    var popularVideosToday = videoClickedRepository.findMostPopularVideosSince(ofType, last24Hours());

    var sortedByPopularity = popularVideosToday.stream()
        .limit(10)
        .map(video -> new PopularTodayGlobally(video.getVideo(), video.getClicks()))
        .sorted(Comparator.comparingLong(PopularTodayGlobally::getClicks))
        .toList();

    popularTodayGloballyRepository.saveAll(sortedByPopularity);
  }


  @Scheduled(cron = "0 0 * * * *")
  @Transactional
  void updatePopularShowInCountries() {
    logger.info("Updating popular videos for each country.");
    popularTodayInCountryRepository.deleteAllInBatch();

    var countries = countryRepository.findAll();
    for (var country : countries) {
      var existsActivityForCountry = videoClickedRepository.existsByCountryId(country.getId());
      if (!existsActivityForCountry) {
        continue;
      }
      logger.info("Updating popular videos in: " + country.getName());

      updateCountryPopularToday(Movie.class, country);
      updateCountryPopularToday(Show.class, country);
    }

    logger.info("Finished updating popular videos for each country.");
  }

  private void updateCountryPopularToday(Class<? extends Video> ofType, Country country) {
    var popularVideosToday = videoClickedRepository.findMostPopularVideosInCountrySince(
        ofType,
        last24Hours(),
        country.getId()
    );

    var sortedByPopularity = popularVideosToday.stream()
        .limit(10)
        .map(video -> new PopularTodayInCountry(country, video.getVideo(), video.getClicks()))
        .sorted(Comparator.comparingLong(PopularTodayInCountry::getClicks))
        .toList();

    popularTodayInCountryRepository.saveAll(sortedByPopularity);
  }

  private static LocalDateTime last24Hours() {
    return LocalDateTime.now().minusDays(1);
  }
}
