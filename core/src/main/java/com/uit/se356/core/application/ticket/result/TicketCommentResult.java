package com.uit.se356.core.application.ticket.result;

import com.uit.se356.core.domain.entities.ticket.TicketComment;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.upload.FileId;
import java.time.OffsetDateTime;
import java.util.List;

public record TicketCommentResult(
    String id,
    UserId authorId,
    String content,
    List<FileId> evidenceFileIds,
    OffsetDateTime createdAt) {
  public static TicketCommentResult from(TicketComment comment) {
    return new TicketCommentResult(
        comment.getId(),
        comment.getAuthorId(),
        comment.getContent(),
        comment.getEvidenceFileIds(),
        comment.getCreatedAt());
  }
}
