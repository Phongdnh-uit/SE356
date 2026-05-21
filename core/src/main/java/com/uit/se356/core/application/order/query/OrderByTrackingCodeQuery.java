package com.uit.se356.core.application.order.query;

import com.uit.se356.common.dto.Query;
import com.uit.se356.core.application.order.projections.OrderDetailProjection;

public record OrderByTrackingCodeQuery(String trackingCode)
    implements Query<OrderDetailProjection> {}
