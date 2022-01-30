package edu.fer.drumre.backend.core.networking;

import static java.time.format.DateTimeFormatter.ofPattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import edu.fer.drumre.backend.core.Api;
import edu.fer.drumre.backend.video.movie.MovieRemoteRepository;
import edu.fer.drumre.backend.video.show.ShowRemoteRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class NetworkConfig {

  @Bean
  OkHttpClient provideOkHttpClient() {
    return new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build();
  }

  @Bean
  @Qualifier("movie")
  Retrofit provideMovieRetrofit(OkHttpClient okHttpClient) {
    return new Builder()
        .baseUrl(Api.THE_MOVIE_DB_API_BASE)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(theMovieDbGson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build();
  }

  @Bean
  @Qualifier("show")
  Retrofit provideShowRetrofit(OkHttpClient okHttpClient) {
    return new Retrofit.Builder()
        .baseUrl(Api.THE_MOVIE_DB_API_BASE)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(theMovieDbGson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build();
  }

  @Bean
  public MovieRemoteRepository provideRemoteMovieRepository(@Qualifier("movie") Retrofit retrofit) {
    return retrofit.create(MovieRemoteRepository.class);
  }

  @Bean
  public ShowRemoteRepository provideRemoteShowRepository(@Qualifier("show") Retrofit retrofit) {
    return retrofit.create(ShowRemoteRepository.class);
  }

  private static final Gson theMovieDbGson = new GsonBuilder()
      .registerTypeAdapter(
          LocalDate.class,
          (JsonDeserializer<LocalDate>) (element, type, jdc) ->
              LocalDate.parse(element.getAsString(), ofPattern("yyyy-MM-dd"))
      ).create();
}
