package com.uit.se356.core.application.ticket.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.ticket.result.TicketResult;
import java.util.ArrayList;
import java.util.List;

public record AddTicketCommentCommand(String ticketId, String content, List<String> evidenceFileIds)
    implements Command<TicketResult> {
  public AddTicketCommentCommand {
    List<FieldError> errors = new ArrayList<>();
    if (ticketId == null || ticketId.isBlank()) {
      errors.add(
          new FieldError(
              "ticketId",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"ticketId"}));
    }
    if (content == null || content.isBlank()) {
      errors.add(
          new FieldError(
              "content", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"content"}));
    }
    if (evidenceFileIds != null && evidenceFileIds.size() > 5) {
      errors.add(new FieldError("evidenceFileIds", "Max 5 images allowed", null));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
