package edu.fer.drumre.backend.setup;

import edu.fer.drumre.backend.core.Api;
import edu.fer.drumre.backend.video.movie.MovieLocalRepository;
import edu.fer.drumre.backend.video.movie.MovieMapper;
import edu.fer.drumre.backend.video.movie.MovieRemoteRepository;
import edu.fer.drumre.backend.video.movie.MovieService;
import edu.fer.drumre.backend.video.movie.dto.TheMovieDbMovieDiscoverResponse;
import edu.fer.drumre.backend.video.movie.dto.TheMovieDbGenreListResponse;
import edu.fer.drumre.backend.video.show.ShowRemoteRepository;
import edu.fer.drumre.backend.video.show.ShowService;
import edu.fer.drumre.backend.video.show.dto.TheMovieDbShowDiscoverResponse;
import edu.fer.drumre.backend.video.show.dto.TheMovieDbShowJustId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class FetchMoviesAndShows implements CommandLineRunner {

  private final MovieLocalRepository movieLocalRepository;
  private final MovieRemoteRepository movieRemoteRepository;
  private final MovieService movieService;
  private final MovieMapper movieMapper;
  private final ShowRemoteRepository showRemoteRepository;
  private final ShowService showService;
  private final Map<Integer, String> idToGenre = new HashMap<>();

  @Autowired
  public FetchMoviesAndShows(
      MovieLocalRepository movieLocalRepository,
      MovieRemoteRepository movieRemoteRepository,
      MovieMapper movieMapper,
      MovieService movieService,
      ShowRemoteRepository showRemoteRepository,
      ShowService showService
  ) {
    this.movieLocalRepository = movieLocalRepository;
    this.movieRemoteRepository = movieRemoteRepository;
    this.movieMapper = movieMapper;
    this.movieService = movieService;
    this.showRemoteRepository = showRemoteRepository;
    this.showService = showService;
  }

  @Override
  public void run(String... args) {
    // to not spam the API
    if (!movieLocalRepository.findAll().isEmpty()) {
      return;
    }

    downloadTheMovieDbGenres();

    Completable.mergeArray(
        download100MostPopularMovies(),
        download100MostPopularShows()
    ).blockingAwait();
  }

  private void downloadTheMovieDbGenres() {
    Single.concat(
            movieRemoteRepository.fetchMovieGenres(Api.THE_MOVIE_DB_API_KEY),
            showRemoteRepository.fetchShowGenres(Api.THE_MOVIE_DB_API_KEY)
        )
        .subscribeOn(Schedulers.io())
        .flatMapIterable(TheMovieDbGenreListResponse::getGenres)
        .blockingSubscribe(genre -> idToGenre.put(genre.getId(), genre.getName()));
  }

  private Completable download100MostPopularShows() {
    return Observable.range(1, 5)
        .subscribeOn(Schedulers.io())
        .concatMapSingle(page -> showRemoteRepository.fetchShowsForDatabase(Api.THE_MOVIE_DB_API_KEY, page))
        .concatMapIterable(TheMovieDbShowDiscoverResponse::getResults)
        .map(TheMovieDbShowJustId::getId)
        .concatMapSingle(showId -> showRemoteRepository.fetchShowDetails(showId, Api.THE_MOVIE_DB_API_KEY))
        .flatMapSingle(showService::addFromTheMovieDb)
        .ignoreElements();
  }

  private Completable download100MostPopularMovies() {
    return Observable.range(1, 5)
        .subscribeOn(Schedulers.io())
        .concatMapSingle(page -> movieRemoteRepository.fetchMoviesForDatabase(Api.THE_MOVIE_DB_API_KEY, page))
        .concatMapIterable(TheMovieDbMovieDiscoverResponse::getResults)
        .map(movieWithGenreIds -> movieMapper.mapGenreIdsToName(movieWithGenreIds, idToGenre::get))
        .flatMapSingle(movieService::addFromTheMovieDb)
        .ignoreElements();
  }
}
