package com.google.sps.data;
import lombok.AllArgsConstructor;

/** Comment */
@AllArgsConstructor
public final class Comment {
  private final long id;
  private final String author;
  private final String comment;
  private final long timestamp;
}
