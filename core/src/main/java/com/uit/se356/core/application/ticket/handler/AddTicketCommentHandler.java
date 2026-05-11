package com.uit.se356.core.application.ticket.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.application.ticket.command.AddTicketCommentCommand;
import com.uit.se356.core.application.ticket.port.out.TicketRepository;
import com.uit.se356.core.application.ticket.result.TicketResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.ticket.Ticket;
import com.uit.se356.core.domain.entities.ticket.TicketComment;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.ticket.TicketId;
import com.uit.se356.core.domain.vo.upload.FileId;

public class AddTicketCommentHandler
    implements CommandHandler<AddTicketCommentCommand, TicketResult> {
  private final TicketRepository ticketRepository;
  private final SecurityUtil<UserId> securityUtil;

  public AddTicketCommentHandler(
      TicketRepository ticketRepository, SecurityUtil<UserId> securityUtil) {
    this.ticketRepository = ticketRepository;
    this.securityUtil = securityUtil;
  }

  @HasPermission(
      name = "Update Ticket",
      description = "Permission to update (comment on) a ticket",
      resource = PermissionConstant.Resource.TICKET,
      action = PermissionConstant.Action.UPDATE)
  @Override
  public TicketResult handle(AddTicketCommentCommand command) {
    UserId currentUserId =
        securityUtil
            .getCurrentUserPrincipal()
            .orElseThrow(() -> new AppException(CommonErrorCode.UNAUTHORIZED))
            .getId();

    Ticket ticket =
        ticketRepository
            .findById(new TicketId(command.ticketId()))
            .orElseThrow(() -> new AppException(CommonErrorCode.RESOURCE_NOT_FOUND));

    // Kiểm tra quyền: Chỉ reporter hoặc CSKH mới được comment
    // Trong thực tế, TICKET_MANAGE sẽ được gán cho CSKH
    // Ở đây ta có thể kiểm tra thêm logic business nếu cần

    TicketComment comment =
        TicketComment.create(
            currentUserId,
            command.content(),
            command.evidenceFileIds() != null
                ? command.evidenceFileIds().stream().map(FileId::new).toList()
                : null);

    ticket.addComment(comment);

    Ticket updatedTicket = ticketRepository.save(ticket);
    return TicketResult.from(updatedTicket);
  }
}
