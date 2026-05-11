package com.uit.se356.core.presentation.dto.ticket;

import com.uit.se356.core.domain.constants.TicketStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessTicketRequest {
  private TicketStatus action;
  private String resolutionNote;
}
