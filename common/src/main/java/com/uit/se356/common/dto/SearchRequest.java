package com.uit.se356.common.dto;

import java.util.List;

public record SearchRequest(String filter, List<String> sorts) {
  public SearchRequest {
    if (filter == null || filter.isBlank()) {
      filter = "";
    }
    sorts = (sorts == null || sorts.isEmpty()) ? List.of("id:asc") : sorts;
  }
}
