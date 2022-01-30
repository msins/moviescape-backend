package edu.fer.drumre.backend.core.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PagingResponse<T>(Paging paging, List<? extends T> records) {

  public static <T> PagingResponse<T> create(Page<? extends T> page) {
    return new PagingResponse<>(
        Paging.create(page),
        page.getContent()
    );
  }

  private record Paging(long total, long page, long pages) {

    public static Paging create(Page<?> page) {
      return new Paging(
          page.getTotalElements(),
          page.getPageable().getPageNumber(),
          page.getTotalPages()
      );
    }
  }

}
