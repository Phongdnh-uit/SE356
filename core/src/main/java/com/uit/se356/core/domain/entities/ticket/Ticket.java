package com.uit.se356.core.domain.entities.ticket;

import com.uit.se356.core.domain.constants.TicketStatus;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.ticket.TicketId;
import com.uit.se356.core.domain.vo.upload.FileId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ticket {
  private final TicketId id;
  private final UserId reporterId;
  private UserId handlerId;
  private final String summary;
  private final String description;
  private TicketStatus status;
  private String resolutionNote;
  private final List<FileId> evidenceFileIds;
  private List<TicketComment> comments;

  private Ticket(
      TicketId id,
      UserId reporterId,
      UserId handlerId,
      String summary,
      String description,
      TicketStatus status,
      String resolutionNote,
      List<FileId> evidenceFileIds,
      List<TicketComment> comments) {
    this.id = id;
    this.reporterId = reporterId;
    this.handlerId = handlerId;
    this.summary = summary;
    this.description = description;
    this.status = status;
    this.resolutionNote = resolutionNote;
    this.evidenceFileIds = evidenceFileIds;
    this.comments = comments;
  }

  public static Ticket create(
      TicketId id,
      UserId reporterId,
      String summary,
      String description,
      List<FileId> evidenceFileIds) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(reporterId);
    Objects.requireNonNull(summary);
    Objects.requireNonNull(description);
    if (evidenceFileIds != null && evidenceFileIds.size() > 5) {
      throw new IllegalArgumentException("Max 5 images allowed");
    }
    return new Ticket(
        id,
        reporterId,
        null,
        summary,
        description,
        TicketStatus.PENDING,
        null,
        evidenceFileIds != null ? evidenceFileIds : List.of(),
        new ArrayList<>());
  }

  public static Ticket rehydrate(
      TicketId id,
      UserId reporterId,
      UserId handlerId,
      String summary,
      String description,
      TicketStatus status,
      String resolutionNote,
      List<FileId> evidenceFileIds,
      List<TicketComment> comments) {
    return new Ticket(
        id,
        reporterId,
        handlerId,
        summary,
        description,
        status,
        resolutionNote,
        evidenceFileIds,
        comments);
  }

  public void addComment(TicketComment comment) {
    Objects.requireNonNull(comment);
    this.comments.add(comment);
    // Nếu staff trả lời, chuyển sang IN_PROGRESS
    if (!comment.getAuthorId().equals(this.reporterId)) {
      this.status = TicketStatus.IN_PROGRESS;
      this.handlerId = comment.getAuthorId();
    }
  }

  public void process(UserId handlerId, TicketStatus newStatus, String resolutionNote) {
    Objects.requireNonNull(handlerId);
    if (newStatus != TicketStatus.APPROVED && newStatus != TicketStatus.REJECTED) {
      throw new IllegalArgumentException("Invalid status for processing");
    }
    this.handlerId = handlerId;
    this.status = newStatus;
    this.resolutionNote = resolutionNote;
  }

  public TicketId getId() {
    return id;
  }

  public UserId getReporterId() {
    return reporterId;
  }

  public UserId getHandlerId() {
    return handlerId;
  }

  public String getSummary() {
    return summary;
  }

  public String getDescription() {
    return description;
  }

  public TicketStatus getStatus() {
    return status;
  }

  public String getResolutionNote() {
    return resolutionNote;
  }

  public List<FileId> getEvidenceFileIds() {
    return evidenceFileIds;
  }

  public List<TicketComment> getComments() {
    return comments;
  }
}
