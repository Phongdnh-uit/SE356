package com.uit.se356.core.application.user.handler;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.application.user.projections.UserSummaryProjection;
import com.uit.se356.core.application.user.query.GetUsersByStatusQuery;

public class GetUsersByStatusHandler
    implements QueryHandler<GetUsersByStatusQuery, PageResponse<UserSummaryProjection>> {

  private final UserRepository userRepository;

  public GetUsersByStatusHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public PageResponse<UserSummaryProjection> handle(GetUsersByStatusQuery query) {
    return userRepository.findByStatusProjection(query.status(), query.pageable());
  }
}
