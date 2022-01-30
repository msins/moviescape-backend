package edu.fer.drumre.backend.video.movie;

import edu.fer.drumre.backend.core.exceptions.EntityWithIdNotFound;
import edu.fer.drumre.backend.core.exceptions.EntityWithNaturalIdExists;
import edu.fer.drumre.backend.location.Country;
import edu.fer.drumre.backend.user.User;
import edu.fer.drumre.backend.video.movie.dto.TheMovieDbMovieResponse;
import edu.fer.drumre.backend.video.popular.PopularityService;
import edu.fer.drumre.backend.video.recommendations.RecommendationService;
import edu.fer.drumre.backend.video.shared.Genre;
import edu.fer.drumre.backend.video.shared.GenreRepository;
import io.reactivex.rxjava3.core.Single;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {

  private final MovieLocalRepository movieRepository;
  private final GenreRepository genreRepository;
  RecommendationService recommendationService;
  private final PopularityService popularityService;
  private final MovieMapper movieMapper;

  @Autowired
  public MovieService(
      MovieLocalRepository movieRepository,
      GenreRepository genreRepository,
      RecommendationService recommendationService,
      PopularityService popularityService,
      MovieMapper movieMapper
  ) {
    this.movieRepository = movieRepository;
    this.genreRepository = genreRepository;
    this.recommendationService = recommendationService;
    this.popularityService = popularityService;
    this.movieMapper = movieMapper;
  }

  @Transactional(readOnly = true)
  public Single<Movie> getMovieDetails(UUID movieUuid) {
    return Single.create(subscriber -> {
      var movieOptional = movieRepository.findByUuid(movieUuid);
      if (movieOptional.isEmpty()) {
        subscriber.onError(new EntityWithIdNotFound(Movie.class, movieUuid));
        return;
      }

      var movie = movieOptional.get();
      subscriber.onSuccess(movie);
    });
  }

  @Transactional(readOnly = true)
  public Single<Page<Movie>> getPopularMoviesToday(Pageable pageable) {
    return popularityService.getMostPopularVideosGloballyToday(
        Movie.class,
        pageable
    );
  }

  @Transactional(readOnly = true)
  public Single<Page<Movie>> getPopularMoviesForCountry(Country country, Pageable pageable) {
    return popularityService.getMostPopularVideosInCountryToday(
        Movie.class,
        country,
        pageable
    );
  }

  @Transactional(readOnly = true)
  public Single<Page<Movie>> getRecommendedMovies(User user, Pageable pageable) {
    return recommendationService.getRecommendedVideos(
        Movie.class,
        user,
        pageable
    );
  }

  @Transactional
  public Single<Movie> addFromTheMovieDb(TheMovieDbMovieResponse response) {
    return Single.create(subscriber -> {
      boolean movieExists = movieRepository.existsByTitleAndReleaseDate(
          response.title(),
          response.releaseDate()
      );

      if (movieExists) {
        subscriber.onError(new EntityWithNaturalIdExists(
            Movie.class,
            String.format("[%s, %d]", response.title(), response.releaseDate().getYear()))
        );
        return;
      }

      Movie newMovie = movieMapper.remoteToLocal(response);
      newMovie.setUuid(UUID.randomUUID());

      for (var genreName : response.genres()) {
        var genre = genreRepository.findByNameEager(genreName)
            .orElse(Genre.fromName(genreName));
        genreRepository.save(genre);
        newMovie.addGenre(genre);
      }

      subscriber.onSuccess(movieRepository.save(newMovie));
    });
  }
}
