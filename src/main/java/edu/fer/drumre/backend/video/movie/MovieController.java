package edu.fer.drumre.backend.video.movie;

import edu.fer.drumre.backend.core.dto.PagingResponse;
import edu.fer.drumre.backend.core.networking.OffsetBasedPageRequest;
import edu.fer.drumre.backend.location.CountryService;
import edu.fer.drumre.backend.user.UserService;
import edu.fer.drumre.backend.video.analytics.AnalyticsService;
import edu.fer.drumre.backend.video.movie.dto.MovieDetailsResponse;
import edu.fer.drumre.backend.video.movie.dto.MovieResponse;
import io.reactivex.rxjava3.core.Single;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/movie")
public class MovieController {

  private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

  private static final String DEFAULT_LIMIT = "10";

  private final MovieMapper movieMapper;
  private final MovieService movieService;
  private final UserService userService;
  private final AnalyticsService analyticsService;
  private final CountryService countryService;

  public MovieController(
      MovieMapper movieMapper,
      MovieService movieService,
      UserService userService,
      AnalyticsService analyticsService,
      CountryService countryService
  ) {
    this.movieMapper = movieMapper;
    this.movieService = movieService;
    this.userService = userService;
    this.analyticsService = analyticsService;
    this.countryService = countryService;
  }

  @GetMapping("popular")
  public Single<ResponseEntity<PagingResponse<MovieResponse>>> getPopularMovies(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) int limit
  ) {
    return movieService.getPopularMoviesToday(OffsetBasedPageRequest.of(offset, limit))
        .map(movieMapper::localToRemote)
        .map(PagingResponse::create)
        .map(ResponseEntity::ok);
  }

  @GetMapping("popular/{countryCode}")
  public Single<ResponseEntity<PagingResponse<MovieResponse>>> getPopularMoviesForCountry(
      @PathVariable("countryCode") String countryCode,
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) int limit
  ) {
    return countryService.getCountryByISO2Code(countryCode)
        .flatMap(country -> movieService.getPopularMoviesForCountry(
                country,
                OffsetBasedPageRequest.of(offset, limit)
            )
        )
        .map(movieMapper::localToRemote)
        .map(PagingResponse::create)
        .map(ResponseEntity::ok);
  }

  @GetMapping("recommendation")
  public Single<ResponseEntity<PagingResponse<MovieResponse>>> getRecommendedMovies(
      @RequestHeader(value = "Authorization") String authToken,
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) int limit
  ) {
    return userService.getUserByToken(authToken)
        .flatMap(user -> movieService.getRecommendedMovies(
                    user,
                    OffsetBasedPageRequest.of(offset, limit)
                )
                .map(movieMapper::localToRemote)
                .map(PagingResponse::create)
                .map(ResponseEntity::ok)
        );
  }

  @GetMapping("details/{movieId}/{countryCode}")
  public Single<ResponseEntity<MovieDetailsResponse>> getMovieDetails(
      @PathVariable("movieId") UUID movieUuid,
      @PathVariable(value = "countryCode") String countryCode
  ) {
    return countryService.getCountryByISO2Code(countryCode)
        .flatMap(country -> movieService.getMovieDetails(movieUuid)
            .doOnSuccess(movie -> analyticsService.videoClicked(movie, country)
                .doOnError(error -> logger.error("Failed to track click for movie: " + movie.getUuid()))
                .subscribe()
            )
            .map(movieMapper::toMovieDetails)
            .map(ResponseEntity::ok)
        );
  }

  @GetMapping("details/{movieId}")
  public Single<ResponseEntity<MovieDetailsResponse>> getMovieDetails(
      @PathVariable("movieId") UUID movieUuid
  ) {
    return movieService.getMovieDetails(movieUuid)
        .doOnSuccess(movie -> analyticsService.videoClicked(movie, null)
            .doOnError(error -> logger.error("Failed to track click for movie: " + movieUuid))
            .subscribe()
        )
        .map(movieMapper::toMovieDetails)
        .map(ResponseEntity::ok);
  }
}
