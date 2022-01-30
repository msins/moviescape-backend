package edu.fer.drumre.backend.core.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public interface LocalToRemote<Local, Remote> {

  Remote localToRemote(Local item);

  default List<Remote> localToRemote(List<Local> items) {
    return items.stream().map(this::localToRemote).toList();
  }

  default Set<Remote> localToRemote(Set<Local> items) {
    return items.stream().map(this::localToRemote).collect(Collectors.toSet());
  }

  default Page<Remote> localToRemote(Page<Local> items) {
    return items.map(this::localToRemote);
  }

}