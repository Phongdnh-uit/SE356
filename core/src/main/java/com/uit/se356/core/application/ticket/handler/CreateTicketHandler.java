package com.uit.se356.core.application.ticket.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.application.ticket.command.CreateTicketCommand;
import com.uit.se356.core.application.ticket.port.out.TicketRepository;
import com.uit.se356.core.application.ticket.result.TicketResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.ticket.Ticket;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.ticket.TicketId;
import com.uit.se356.core.domain.vo.upload.FileId;

public class CreateTicketHandler implements CommandHandler<CreateTicketCommand, TicketResult> {
  private final TicketRepository ticketRepository;
  private final IdGenerator idGenerator;
  private final SecurityUtil<UserId> securityUtil;

  public CreateTicketHandler(
      TicketRepository ticketRepository,
      IdGenerator idGenerator,
      SecurityUtil<UserId> securityUtil) {
    this.ticketRepository = ticketRepository;
    this.idGenerator = idGenerator;
    this.securityUtil = securityUtil;
  }

  @HasPermission(
      name = "Create Ticket",
      description = "Permission to create a new ticket",
      resource = PermissionConstant.Resource.TICKET,
      action = PermissionConstant.Action.CREATE)
  @Override
  public TicketResult handle(CreateTicketCommand command) {
    UserId currentUserId =
        securityUtil
            .getCurrentUserPrincipal()
            .orElseThrow(() -> new AppException(CommonErrorCode.UNAUTHORIZED))
            .getId();

    Ticket ticket =
        Ticket.create(
            new TicketId(idGenerator.generate().toString()),
            currentUserId,
            command.summary(),
            command.description(),
            command.evidenceFileIds() != null
                ? command.evidenceFileIds().stream().map(FileId::new).toList()
                : null);

    Ticket savedTicket = ticketRepository.save(ticket);
    return TicketResult.from(savedTicket);
  }
}
