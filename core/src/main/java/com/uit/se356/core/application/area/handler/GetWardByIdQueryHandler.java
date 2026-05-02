package com.uit.se356.core.application.area.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.area.port.WardRepository;
import com.uit.se356.core.application.area.query.GetWardByIdQuery;
import com.uit.se356.core.application.area.result.WardResult;
import com.uit.se356.core.domain.entities.area.Ward;
import com.uit.se356.core.domain.exception.AreaErrorCode;

public class GetWardByIdQueryHandler implements QueryHandler<GetWardByIdQuery, WardResult> {

  private final WardRepository wardRepository;

  public GetWardByIdQueryHandler(WardRepository wardRepository) {
    this.wardRepository = wardRepository;
  }

  @Override
  public WardResult handle(GetWardByIdQuery query) {
    Ward ward =
        wardRepository
            .findById(query.id())
            .orElseThrow(() -> new AppException(AreaErrorCode.WARD_NOT_FOUND));
    return WardResult.fromEntity(ward);
  }
}
