package edu.fer.drumre.backend.core.dto;

import java.util.List;

public interface RemoteToLocal<Remote, Local> {

  Local remoteToLocal(Remote item);

  default List<Local> remoteToLocal(List<Remote> items) {
    return items.stream().map(this::remoteToLocal).toList();
  }
}
