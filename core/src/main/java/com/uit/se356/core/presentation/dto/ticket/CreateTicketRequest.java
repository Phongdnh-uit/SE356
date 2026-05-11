package com.uit.se356.core.presentation.dto.ticket;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTicketRequest {
  private String summary;
  private String description;
  private List<String> evidenceFileIds;
}
