package com.uit.se356.core.application.user.handler;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.application.user.projections.UserSummaryProjection;
import com.uit.se356.core.application.user.query.GetAllUserProfilesQuery;

public class UserSummaryQueryHandler
    implements QueryHandler<GetAllUserProfilesQuery, PageResponse<UserSummaryProjection>> {

  private final UserRepository userRepository;

  public UserSummaryQueryHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public PageResponse<UserSummaryProjection> handle(GetAllUserProfilesQuery query) {
    return userRepository.findAllProjection(query.pageable());
  }
}
