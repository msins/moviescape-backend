package edu.fer.drumre.backend.video.movie;


import edu.fer.drumre.backend.video.movie.dto.TheMovieDbConfigurationResponse;
import edu.fer.drumre.backend.video.movie.dto.TheMovieDbMovieDiscoverResponse;
import edu.fer.drumre.backend.video.movie.dto.TheMovieDbGenreListResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieRemoteRepository {

  @GET("discover/movie?sort_by=popularity.desc&language=en-US")
  Single<TheMovieDbMovieDiscoverResponse> fetchMoviesForDatabase(
      @Query("api_key") String apiKey,
      @Query("page") int page
  );

  @GET("configuration")
  Single<TheMovieDbConfigurationResponse> fetchConfiguration(
      @Query("api_key") String apiKey
  );

  @GET("genre/movie/list")
  Single<TheMovieDbGenreListResponse> fetchMovieGenres(
      @Query("api_key") String apiKey
  );
}

