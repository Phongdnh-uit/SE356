package com.uit.se356.core.application.ticket.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.ticket.result.TicketResult;
import java.util.ArrayList;
import java.util.List;

public record CreateTicketCommand(String summary, String description, List<String> evidenceFileIds)
    implements Command<TicketResult> {
  public CreateTicketCommand {
    List<FieldError> errors = new ArrayList<>();
    if (summary == null || summary.isBlank()) {
      errors.add(
          new FieldError(
              "summary", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"summary"}));
    }
    if (description == null || description.isBlank()) {
      errors.add(
          new FieldError(
              "description",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"description"}));
    }
    if (evidenceFileIds != null && evidenceFileIds.size() > 5) {
      errors.add(new FieldError("evidenceFileIds", "Max 5 images allowed", null));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
