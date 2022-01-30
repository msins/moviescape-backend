package edu.fer.drumre.backend.video.shared;

import edu.fer.drumre.backend.core.dto.LocalToRemote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class GenreMapper implements LocalToRemote<Genre, GenreResponse> {

  @Override
  public GenreResponse localToRemote(Genre item) {
    return new GenreResponse(item.getName());
  }
}
