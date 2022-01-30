package edu.fer.drumre.backend.video.show;

import edu.fer.drumre.backend.core.Api;
import edu.fer.drumre.backend.core.dto.LocalToRemote;
import edu.fer.drumre.backend.core.dto.RemoteToLocal;
import edu.fer.drumre.backend.video.movie.dto.MovieDetailsResponse;
import edu.fer.drumre.backend.video.shared.GenreMapper;
import edu.fer.drumre.backend.video.show.dto.ShowDetailsResponse;
import edu.fer.drumre.backend.video.show.dto.ShowResponse;
import edu.fer.drumre.backend.video.show.dto.TheMovieDbShowDetailsResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = GenreMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ShowMapper implements
    LocalToRemote<Show, ShowResponse>,
    RemoteToLocal<TheMovieDbShowDetailsResponse, Show> {

  private GenreMapper genreMapper;

  @Autowired
  private void setGenreMapper(GenreMapper genreMapper) {
    this.genreMapper = genreMapper;
  }

  @Override
  public ShowResponse localToRemote(Show item) {
    return new ShowResponse(
        item.getUuid(),
        item.getTitle(),
        item.getReleaseDate().getYear(),
        item.getNumOfEpisodes(),
        item.getNumOfSeasons(),
        item.getCoverUrl(),
        genreMapper.localToRemote(item.getGenres())
    );
  }

  public ShowDetailsResponse toShowDetails(Show item) {
    return new ShowDetailsResponse(
        item.getUuid(),
        item.getTitle(),
        item.getReleaseDate().getYear(),
        item.getNumOfEpisodes(),
        item.getNumOfSeasons(),
        item.getCoverUrl(),
        genreMapper.localToRemote(item.getGenres()),
        item.getDescription()
    );
  }

  @Override
  public Show remoteToLocal(TheMovieDbShowDetailsResponse item) {
    Show show = new Show();
    show.setTitle(item.getTitle());
    if (item.getPosterPath() == null) {
      show.setCoverUrl(null);
    } else {
      show.setCoverUrl(Api.TMDB_CDN_BASE_URL + item.getPosterPath().substring(1));
    }
    show.setReleaseDate(item.getReleaseDate());
    show.setDescription(item.getDescription());
    show.setNumOfEpisodes(item.getNumberOfEpisodes());
    show.setNumOfSeasons(item.getNumberOfSeasons());
    return show;
  }
}
