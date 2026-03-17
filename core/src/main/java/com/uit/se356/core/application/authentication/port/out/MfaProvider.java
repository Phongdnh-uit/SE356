package com.uit.se356.core.application.authentication.port.out;

import com.uit.se356.core.application.authentication.result.mfa.MfaChallengeResult;
import com.uit.se356.core.application.authentication.result.mfa.MfaSetupResult;
import com.uit.se356.core.application.authentication.result.mfa.MfaVerifyResult;
import com.uit.se356.core.domain.vo.authentication.MfaMethod;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.mfa.MfaConfig;

public interface MfaProvider {

  boolean supports(MfaMethod method);

  MfaSetupResult<? extends MfaConfig> initiateMfaSetup(UserId userId);

  MfaChallengeResult initiateMfaChallenge(UserId userId, MfaMethod method);

  MfaVerifyResult verify(MfaConfig config, String credential);
}
