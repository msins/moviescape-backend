package edu.fer.drumre.backend.video.movie;

import edu.fer.drumre.backend.core.Api;
import edu.fer.drumre.backend.core.dto.LocalToRemote;
import edu.fer.drumre.backend.core.dto.RemoteToLocal;
import edu.fer.drumre.backend.video.movie.dto.MovieDetailsResponse;
import edu.fer.drumre.backend.video.movie.dto.MovieResponse;
import edu.fer.drumre.backend.video.movie.dto.TheMovieDbMovieResponse;
import edu.fer.drumre.backend.video.movie.dto.TheMovieDbMovieWithGenreIdsResponse;
import edu.fer.drumre.backend.video.shared.GenreMapper;
import java.util.function.Function;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = GenreMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class MovieMapper implements
    LocalToRemote<Movie, MovieResponse>,
    RemoteToLocal<TheMovieDbMovieResponse, Movie> {

  private GenreMapper genreMapper;

  @Autowired
  public void setGenreMapper(GenreMapper genreMapper) {
    this.genreMapper = genreMapper;
  }

  @Override
  public MovieResponse localToRemote(Movie item) {
    return new MovieResponse(
        item.getUuid(),
        item.getTitle(),
        item.getReleaseDate().getYear(),
        item.getCoverUrl(),
        genreMapper.localToRemote(item.getGenres())
    );
  }

  public MovieDetailsResponse toMovieDetails(Movie item) {
    return new MovieDetailsResponse(
        item.getUuid(),
        item.getTitle(),
        item.getReleaseDate().getYear(),
        item.getCoverUrl(),
        genreMapper.localToRemote(item.getGenres()),
        item.getDescription()
    );
  }

  @Override
  public Movie remoteToLocal(TheMovieDbMovieResponse item) {
    var movie = new Movie();
    movie.setTitle(item.title());
    movie.setCoverUrl(item.coverUrl());
    movie.setReleaseDate(item.releaseDate());
    movie.setDescription(item.description());
    return movie;
  }

  public TheMovieDbMovieResponse mapGenreIdsToName(
      TheMovieDbMovieWithGenreIdsResponse item,
      Function<Integer, String> genreNameForId
  ) {
    return new TheMovieDbMovieResponse(
        item.getTitle(),
        Api.TMDB_CDN_BASE_URL + item.getPosterPath().substring(1),
        item.getReleaseDate(),
        item.getGenreIds().stream().map(genreNameForId).toList(),
        item.getDescription()
    );
  }
}
