package com.uit.se356.core.domain.exception;

import com.uit.se356.common.exception.ErrorCode;

public enum VehicleErrorCode implements ErrorCode {
  VEHICLE_NOT_FOUND("VEH_001", "error.vehicle.not_found", 404),
  DUPLICATE_LICENSE_PLATE("VEH_002", "error.vehicle.duplicate_license_plate", 400),
  SHIPPER_ALREADY_ASSIGNED("VEH_003", "error.vehicle.shipper_already_assigned", 400),
  INVALID_CAPACITY("VEH_004", "error.vehicle.invalid_capacity", 400),
  INVALID_VEHICLE_ID("VEH_005", "error.vehicle.invalid_vehicle_id", 400);

  private final String code;
  private final String messageKey;
  private final int httpStatus;

  VehicleErrorCode(String code, String messageKey, int httpStatus) {
    this.code = code;
    this.messageKey = messageKey;
    this.httpStatus = httpStatus;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getMessageKey() {
    return messageKey;
  }

  @Override
  public int getHttpStatus() {
    return httpStatus;
  }
}
