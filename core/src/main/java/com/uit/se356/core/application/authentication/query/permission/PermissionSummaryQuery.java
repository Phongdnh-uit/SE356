package com.uit.se356.core.application.authentication.query.permission;

import com.uit.se356.common.dto.Query;
import com.uit.se356.common.dto.SearchRequest;
import com.uit.se356.core.application.authentication.projections.PermissionSummaryProjection;
import java.util.List;
import java.util.Map;

public record PermissionSummaryQuery(SearchRequest pageable)
    implements Query<Map<String, List<PermissionSummaryProjection>>> {}
