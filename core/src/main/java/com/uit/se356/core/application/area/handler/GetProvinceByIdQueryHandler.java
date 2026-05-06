package com.uit.se356.core.application.area.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.area.port.ProvinceRepository;
import com.uit.se356.core.application.area.query.GetProvinceByIdQuery;
import com.uit.se356.core.application.area.result.ProvinceResult;
import com.uit.se356.core.domain.entities.area.Province;
import com.uit.se356.core.domain.exception.AreaErrorCode;

public class GetProvinceByIdQueryHandler
    implements QueryHandler<GetProvinceByIdQuery, ProvinceResult> {

  private final ProvinceRepository provinceRepository;

  public GetProvinceByIdQueryHandler(ProvinceRepository provinceRepository) {
    this.provinceRepository = provinceRepository;
  }

  @Override
  public ProvinceResult handle(GetProvinceByIdQuery query) {
    Province province =
        provinceRepository
            .findById(query.id())
            .orElseThrow(() -> new AppException(AreaErrorCode.PROVINCE_NOT_FOUND));
    return ProvinceResult.fromEntity(province);
  }
}
