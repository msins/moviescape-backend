package edu.fer.drumre.backend.video.show;

import edu.fer.drumre.backend.video.movie.dto.TheMovieDbGenreListResponse;
import edu.fer.drumre.backend.video.show.dto.TheMovieDbShowDetailsResponse;
import edu.fer.drumre.backend.video.show.dto.TheMovieDbShowDiscoverResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShowRemoteRepository {

  @GET("discover/tv?sort_by=popularity.desc&language=en-US")
  Single<TheMovieDbShowDiscoverResponse> fetchShowsForDatabase(
      @Query("api_key") String apiKey,
      @Query("page") int page
  );

  @GET("genre/tv/list")
  Single<TheMovieDbGenreListResponse> fetchShowGenres(
      @Query("api_key") String apiKey
  );

  @GET("tv/{showId}")
  Single<TheMovieDbShowDetailsResponse> fetchShowDetails(
      @Path("showId") int showId,
      @Query("api_key") String apiKey
  );
}
