package com.uit.se356.core.application.internal.handler;

import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.authentication.port.out.AuthCacheRepository;
import com.uit.se356.core.application.internal.query.DebugOtpQuery;
import com.uit.se356.core.application.internal.result.DebugOtpResult;
import com.uit.se356.core.domain.constants.CacheKey;
import com.uit.se356.core.domain.constants.PermissionConstant;

public class DebugOtpHandler implements QueryHandler<DebugOtpQuery, DebugOtpResult> {

  private final AuthCacheRepository cacheRepository;

  public DebugOtpHandler(AuthCacheRepository cacheRepository) {
    this.cacheRepository = cacheRepository;
  }

  @HasPermission(
      name = "Get Debug OTP",
      description = "Permission to read OTP for debugging purposes",
      resource = PermissionConstant.Resource.INTERNAL,
      action = PermissionConstant.Action.READ)
  @Override
  public DebugOtpResult handle(DebugOtpQuery query) {
    String cacheKey = CacheKey.PHONE_VERIFICATION_CODE_PREFIX + ":" + query.phoneNumber();
    return cacheRepository
        .get(cacheKey)
        .map(otp -> new DebugOtpResult(otp))
        .orElseGet(() -> new DebugOtpResult(null));
  }
}
