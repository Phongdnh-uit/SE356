package com.uit.se356.core.domain.entities.ticket;

import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.upload.FileId;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TicketComment {
  private final String id;
  private final UserId authorId;
  private final String content;
  private final List<FileId> evidenceFileIds;
  private final OffsetDateTime createdAt;

  private TicketComment(
      String id,
      UserId authorId,
      String content,
      List<FileId> evidenceFileIds,
      OffsetDateTime createdAt) {
    this.id = id;
    this.authorId = authorId;
    this.content = content;
    this.evidenceFileIds = evidenceFileIds;
    this.createdAt = createdAt;
  }

  public static TicketComment create(
      UserId authorId, String content, List<FileId> evidenceFileIds) {
    Objects.requireNonNull(authorId);
    Objects.requireNonNull(content);
    if (evidenceFileIds != null && evidenceFileIds.size() > 5) {
      throw new IllegalArgumentException("Max 5 images allowed");
    }
    return new TicketComment(
        UUID.randomUUID().toString(),
        authorId,
        content,
        evidenceFileIds != null ? evidenceFileIds : List.of(),
        OffsetDateTime.now());
  }

  public static TicketComment rehydrate(
      String id,
      UserId authorId,
      String content,
      List<FileId> evidenceFileIds,
      OffsetDateTime createdAt) {
    return new TicketComment(id, authorId, content, evidenceFileIds, createdAt);
  }

  public String getId() {
    return id;
  }

  public UserId getAuthorId() {
    return authorId;
  }

  public String getContent() {
    return content;
  }

  public List<FileId> getEvidenceFileIds() {
    return evidenceFileIds;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }
}
