package edu.fer.drumre.backend.core.networking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

public class OffsetBasedPageRequest implements Pageable {

  private final int offset;
  private final int limit;
  private final Sort sort;

  private OffsetBasedPageRequest(int offset, int limit, Sort sort) {
    this.offset = offset;
    this.limit = limit;
    this.sort = sort;
  }

  public static OffsetBasedPageRequest of(int offset, int limit, Sort sort) {
    return new OffsetBasedPageRequest(offset, limit, sort);
  }

  public static OffsetBasedPageRequest of(int offset, int limit) {
    return new OffsetBasedPageRequest(offset, limit, Sort.unsorted());
  }

  @Override
  public int getPageNumber() {
    return limit == 0 ? 0 : offset / limit;
  }

  @Override
  public int getPageSize() {
    return limit;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  @Override
  @NonNull
  public Sort getSort() {
    return sort;
  }

  @Override
  @NonNull
  public Pageable next() {
    return new OffsetBasedPageRequest((int) getOffset() + getPageSize(), getPageSize(), getSort());
  }

  public OffsetBasedPageRequest previous() {
    return hasPrevious()
        ? new OffsetBasedPageRequest((int) getOffset() - getPageSize(), getPageSize(), getSort())
        : this;
  }

  @Override
  @NonNull
  public Pageable previousOrFirst() {
    return hasPrevious() ? previous() : first();
  }

  @Override
  @NonNull
  public Pageable first() {
    return new OffsetBasedPageRequest(0, getPageSize(), getSort());
  }

  @Override
  @NonNull
  public Pageable withPage(int i) {
    return new OffsetBasedPageRequest(i * getPageSize(), getPageSize(), getSort());
  }

  @Override
  public boolean hasPrevious() {
    return offset >= limit;
  }
}