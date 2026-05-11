package com.uit.se356.core.application.ticket.port.out;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.domain.entities.ticket.Ticket;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.ticket.TicketId;
import java.util.Optional;

public interface TicketRepository {
  Ticket save(Ticket ticket);

  Optional<Ticket> findById(TicketId id);

  PageResponse<Ticket> findAll(SearchPageable searchCriteria);

  PageResponse<Ticket> findByReporterId(UserId reporterId, SearchPageable searchCriteria);
}
