package com.uit.se356.core.application.ticket.handler;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.application.ticket.port.out.TicketRepository;
import com.uit.se356.core.application.ticket.query.GetMyTicketsQuery;
import com.uit.se356.core.application.ticket.result.TicketResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.vo.authentication.UserId;

public class GetMyTicketsHandler
    implements QueryHandler<GetMyTicketsQuery, PageResponse<TicketResult>> {
  private final TicketRepository ticketRepository;
  private final SecurityUtil<UserId> securityUtil;

  public GetMyTicketsHandler(TicketRepository ticketRepository, SecurityUtil<UserId> securityUtil) {
    this.ticketRepository = ticketRepository;
    this.securityUtil = securityUtil;
  }

  @HasPermission(
      name = "View Own Tickets",
      description = "Permission to view own tickets",
      resource = PermissionConstant.Resource.TICKET,
      action = PermissionConstant.Action.READ)
  @Override
  public PageResponse<TicketResult> handle(GetMyTicketsQuery query) {
    UserId currentUserId =
        securityUtil
            .getCurrentUserPrincipal()
            .orElseThrow(() -> new AppException(CommonErrorCode.UNAUTHORIZED))
            .getId();

    PageResponse<TicketResult> tickets =
        ticketRepository
            .findByReporterId(currentUserId, query.searchCriteria())
            .map(TicketResult::from);

    return tickets;
  }
}
