package com.uit.se356.core.application.ticket.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.application.ticket.command.ProcessTicketCommand;
import com.uit.se356.core.application.ticket.port.out.TicketRepository;
import com.uit.se356.core.application.ticket.result.TicketResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.ticket.Ticket;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.ticket.TicketId;

public class ProcessTicketHandler implements CommandHandler<ProcessTicketCommand, TicketResult> {
  private final TicketRepository ticketRepository;
  private final SecurityUtil<UserId> securityUtil;

  public ProcessTicketHandler(
      TicketRepository ticketRepository, SecurityUtil<UserId> securityUtil) {
    this.ticketRepository = ticketRepository;
    this.securityUtil = securityUtil;
  }

  @HasPermission(
      name = "Manage Ticket",
      description = "Permission to manage (process) a ticket",
      resource = PermissionConstant.Resource.TICKET,
      action = PermissionConstant.Action.UPDATE)
  @Override
  public TicketResult handle(ProcessTicketCommand command) {
    UserId currentUserId =
        securityUtil
            .getCurrentUserPrincipal()
            .orElseThrow(() -> new AppException(CommonErrorCode.UNAUTHORIZED))
            .getId();

    Ticket ticket =
        ticketRepository
            .findById(new TicketId(command.ticketId()))
            .orElseThrow(() -> new AppException(CommonErrorCode.RESOURCE_NOT_FOUND));

    ticket.process(currentUserId, command.action(), command.resolutionNote());

    Ticket updatedTicket = ticketRepository.save(ticket);
    return TicketResult.from(updatedTicket);
  }
}
