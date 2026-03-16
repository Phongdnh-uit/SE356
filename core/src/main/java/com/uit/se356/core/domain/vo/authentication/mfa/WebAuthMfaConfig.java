package com.uit.se356.core.domain.vo.authentication.mfa;

import java.util.List;

public record WebAuthMfaConfig(
    byte[] challenge,
    byte[] credentialId,
    byte[] publicKeyCos,
    long signCount,
    List<String> transports,
    boolean backupEligible,
    boolean backupState)
    implements MfaConfig {}
