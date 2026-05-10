package com.uit.se356.core.application.user.query;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.Query;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.user.projections.UserSummaryProjection;
import com.uit.se356.core.domain.vo.authentication.UserStatus;

public record GetUsersByStatusQuery(UserStatus status, SearchPageable pageable)
    implements Query<PageResponse<UserSummaryProjection>> {}
