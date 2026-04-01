package com.uit.se356.core.application.area.projections;

import com.uit.se356.core.domain.vo.area.ProvinceId;
import com.uit.se356.core.domain.vo.area.WardType;

/** Projection dùng để trả về thông tin tóm tắt của Ward Dùng khi liệt kê danh sách wards */
public interface WardSummaryProjection {
  String getId();

  String getCode();

  String getName();

  WardType getType();

  ProvinceId getProvinceId();

  String getProvinceName();
}
