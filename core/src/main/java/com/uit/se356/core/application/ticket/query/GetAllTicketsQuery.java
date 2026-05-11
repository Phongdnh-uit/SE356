package com.uit.se356.core.application.ticket.query;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.Query;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.ticket.result.TicketResult;

public record GetAllTicketsQuery(SearchPageable searchCriteria)
    implements Query<PageResponse<TicketResult>> {}
