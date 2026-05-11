package com.uit.se356.core.application.ticket.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.ticket.result.TicketResult;
import com.uit.se356.core.domain.constants.TicketStatus;
import java.util.ArrayList;
import java.util.List;

public record ProcessTicketCommand(String ticketId, TicketStatus action, String resolutionNote)
    implements Command<TicketResult> {
  public ProcessTicketCommand {
    List<FieldError> errors = new ArrayList<>();
    if (ticketId == null || ticketId.isBlank()) {
      errors.add(
          new FieldError(
              "ticketId",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"ticketId"}));
    }
    if (action != TicketStatus.APPROVED && action != TicketStatus.REJECTED) {
      errors.add(new FieldError("action", "Action must be APPROVED or REJECTED", null));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
