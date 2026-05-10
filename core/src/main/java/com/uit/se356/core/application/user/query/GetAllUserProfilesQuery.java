package com.uit.se356.core.application.user.query;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.Query;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.user.projections.UserSummaryProjection;

public record GetAllUserProfilesQuery(SearchPageable pageable)
    implements Query<PageResponse<UserSummaryProjection>> {}
