package com.uit.se356.core.application.ticket.handler;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.ticket.port.out.TicketRepository;
import com.uit.se356.core.application.ticket.query.GetAllTicketsQuery;
import com.uit.se356.core.application.ticket.result.TicketResult;
import com.uit.se356.core.domain.constants.PermissionConstant;

public class GetAllTicketsHandler
    implements QueryHandler<GetAllTicketsQuery, PageResponse<TicketResult>> {
  private final TicketRepository ticketRepository;

  public GetAllTicketsHandler(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  @HasPermission(
      name = "Manage Ticket",
      description = "Permission to manage (view all) tickets",
      resource = PermissionConstant.Resource.TICKET,
      action = PermissionConstant.Action.READ)
  @Override
  public PageResponse<TicketResult> handle(GetAllTicketsQuery query) {
    return ticketRepository.findAll(query.searchCriteria()).map(TicketResult::from);
  }
}
