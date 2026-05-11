package com.uit.se356.core.application.ticket.query;

import com.uit.se356.common.dto.Query;
import com.uit.se356.core.application.ticket.result.TicketResult;

public record GetTicketDetailsQuery(String id) implements Query<TicketResult> {}
