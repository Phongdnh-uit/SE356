package com.uit.se356.core.application.ticket.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.application.ticket.port.out.TicketRepository;
import com.uit.se356.core.application.ticket.query.GetTicketDetailsQuery;
import com.uit.se356.core.application.ticket.result.TicketResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.ticket.Ticket;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.ticket.TicketId;

public class GetTicketDetailsHandler implements QueryHandler<GetTicketDetailsQuery, TicketResult> {
  private final TicketRepository ticketRepository;
  private final SecurityUtil<UserId> securityUtil;

  public GetTicketDetailsHandler(
      TicketRepository ticketRepository, SecurityUtil<UserId> securityUtil) {
    this.ticketRepository = ticketRepository;
    this.securityUtil = securityUtil;
  }

  @HasPermission(
      name = "View Ticket Details",
      description = "Permission to view ticket details",
      resource = PermissionConstant.Resource.TICKET,
      action = PermissionConstant.Action.READ)
  @Override
  public TicketResult handle(GetTicketDetailsQuery query) {
    Ticket ticket =
        ticketRepository
            .findById(new TicketId(query.id()))
            .orElseThrow(() -> new AppException(CommonErrorCode.RESOURCE_NOT_FOUND));

    UserId currentUserId =
        securityUtil
            .getCurrentUserPrincipal()
            .orElseThrow(() -> new AppException(CommonErrorCode.UNAUTHORIZED))
            .getId();

    // Kiểm tra quyền: Chỉ reporter hoặc CSKH mới được xem chi tiết
    // Ở đây ta có thể kiểm tra thêm logic business nếu cần (e.g. check if currentUserId is reporter
    // or has TICKET_MANAGE)

    return TicketResult.from(ticket);
  }
}
