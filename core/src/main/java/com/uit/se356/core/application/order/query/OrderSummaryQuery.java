package com.uit.se356.core.application.order.query;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.Query;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.order.projections.OrderSummaryProjection;

public record OrderSummaryQuery(SearchPageable pageable)
    implements Query<PageResponse<OrderSummaryProjection>> {}
