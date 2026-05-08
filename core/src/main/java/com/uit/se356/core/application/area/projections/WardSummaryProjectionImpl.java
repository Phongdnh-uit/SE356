package com.uit.se356.core.application.area.projections;

import com.uit.se356.core.domain.vo.area.ProvinceId;
import com.uit.se356.core.domain.vo.area.WardType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Implementation của WardSummaryProjection với thông tin tóm tắt của Ward kèm Province */
@Getter
@AllArgsConstructor
public class WardSummaryProjectionImpl implements WardSummaryProjection {
  private String id;
  private String code;
  private String name;
  private WardType type;
  private String provinceId;
  private String provinceName;

  @Override
  public ProvinceId getProvinceId() {
    return new ProvinceId(this.provinceId);
  }

  @Override
  public String getProvinceName() {
    return this.provinceName;
  }
}
