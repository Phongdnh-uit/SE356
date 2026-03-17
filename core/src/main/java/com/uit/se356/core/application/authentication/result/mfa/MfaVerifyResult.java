package com.uit.se356.core.application.authentication.result.mfa;

import com.uit.se356.core.domain.vo.authentication.mfa.MfaConfig;

public record MfaVerifyResult(boolean success, MfaConfig updatedConfig) {}
