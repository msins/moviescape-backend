package edu.fer.drumre.backend.video.show;

import edu.fer.drumre.backend.core.exceptions.EntityWithIdNotFound;
import edu.fer.drumre.backend.core.exceptions.EntityWithNaturalIdExists;
import edu.fer.drumre.backend.location.Country;
import edu.fer.drumre.backend.user.User;
import edu.fer.drumre.backend.video.popular.PopularityService;
import edu.fer.drumre.backend.video.recommendations.RecommendationService;
import edu.fer.drumre.backend.video.shared.Genre;
import edu.fer.drumre.backend.video.shared.GenreRepository;
import edu.fer.drumre.backend.video.show.dto.TheMovieDbShowDetailsResponse;
import io.reactivex.rxjava3.core.Single;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShowService {

  private final ShowLocalRepository showRepository;
  private final GenreRepository genreRepository;
  private final RecommendationService recommendationService;
  private final ShowMapper showMapper;
  private final PopularityService popularityService;

  @Autowired
  public ShowService(
      ShowLocalRepository showRepository,
      GenreRepository genreRepository,
      RecommendationService recommendationService,
      ShowMapper showMapper,
      PopularityService popularityService
  ) {
    this.showRepository = showRepository;
    this.genreRepository = genreRepository;
    this.recommendationService = recommendationService;
    this.showMapper = showMapper;
    this.popularityService = popularityService;
  }

  @Transactional(readOnly = true)
  public Single<Show> getShowDetails(UUID showUuid) {
    return Single.create(subscriber -> {
      var showOptional = showRepository.findByUuid(showUuid);
      if (showOptional.isEmpty()) {
        subscriber.onError(new EntityWithIdNotFound(Show.class, showUuid));
        return;
      }

      var show = showOptional.get();
      subscriber.onSuccess(show);
    });
  }

  @Transactional(readOnly = true)
  public Single<Page<Show>> getPopularShows(Pageable pageable) {
    return popularityService.getMostPopularVideosGloballyToday(
        Show.class,
        pageable
    );
  }

  @Transactional(readOnly = true)
  public Single<Page<Show>> getPopularShowsForCountry(Country country, Pageable pageable) {
    return popularityService.getMostPopularVideosInCountryToday(
        Show.class,
        country,
        pageable
    );
  }

  @Transactional(readOnly = true)
  public Single<Page<Show>> getRecommendedShows(User user, Pageable pageable) {
    return recommendationService.getRecommendedVideos(
        Show.class,
        user,
        pageable
    );
  }

  @Transactional
  public Single<Show> addFromTheMovieDb(TheMovieDbShowDetailsResponse response) {
    return Single.create(subscriber -> {
      boolean showExists = showRepository.existsByTitleAndReleaseDate(
          response.getTitle(),
          response.getReleaseDate()
      );

      if (showExists) {
        subscriber.onError(new EntityWithNaturalIdExists(
            Show.class,
            String.format("[%s, %d]", response.getTitle(), response.getReleaseDate().getYear()))
        );
        return;
      }

      Show newShow = showMapper.remoteToLocal(response);
      newShow.setUuid(UUID.randomUUID());

      for (var genreName : response.getGenres()) {
        var genre = genreRepository.findByNameEager(genreName)
            .orElse(Genre.fromName(genreName));
        genreRepository.save(genre);
        newShow.addGenre(genre);
      }

      subscriber.onSuccess(showRepository.save(newShow));
    });
  }
}
