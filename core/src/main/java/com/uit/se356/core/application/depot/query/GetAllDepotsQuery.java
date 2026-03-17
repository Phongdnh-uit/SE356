package com.uit.se356.core.application.depot.query;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.Query;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.depot.projection.DepotSummaryProjection;

public record GetAllDepotsQuery(SearchPageable pageable)
    implements Query<PageResponse<DepotSummaryProjection>> {}
