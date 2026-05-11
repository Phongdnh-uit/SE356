package com.uit.se356.core.application.ticket.result;

import com.uit.se356.core.domain.constants.TicketStatus;
import com.uit.se356.core.domain.entities.ticket.Ticket;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.ticket.TicketId;
import com.uit.se356.core.domain.vo.upload.FileId;
import java.util.List;

public record TicketResult(
    TicketId id,
    UserId reporterId,
    UserId handlerId,
    String summary,
    String description,
    TicketStatus status,
    String resolutionNote,
    List<FileId> evidenceFileIds,
    List<TicketCommentResult> comments) {
  public static TicketResult from(Ticket ticket) {
    return new TicketResult(
        ticket.getId(),
        ticket.getReporterId(),
        ticket.getHandlerId(),
        ticket.getSummary(),
        ticket.getDescription(),
        ticket.getStatus(),
        ticket.getResolutionNote(),
        ticket.getEvidenceFileIds(),
        ticket.getComments().stream().map(TicketCommentResult::from).toList());
  }
}
