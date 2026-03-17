package com.uit.se356.core.application.authentication.handler.mfa;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.authentication.command.IssueTokenCommand;
import com.uit.se356.core.application.authentication.command.mfa.RecoveryMfaCommand;
import com.uit.se356.core.application.authentication.port.in.IssueTokenService;
import com.uit.se356.core.application.authentication.port.out.AuthCacheRepository;
import com.uit.se356.core.application.authentication.port.out.MfaBackupCodeRepository;
import com.uit.se356.core.application.authentication.port.out.PasswordEncoder;
import com.uit.se356.core.application.authentication.result.LoginResult;
import com.uit.se356.core.application.authentication.result.TokenPairResult;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.domain.constants.CacheKey;
import com.uit.se356.core.domain.entities.authentication.MfaBackupCode;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.exception.UserErrorCode;
import com.uit.se356.core.domain.vo.authentication.mfa.MfaChallengeCache;
import java.util.List;
import java.util.Optional;

public class RecoveryMfaHandler implements CommandHandler<RecoveryMfaCommand, LoginResult> {
  private final AuthCacheRepository authCacheRepository;
  private final MfaBackupCodeRepository mfaBackupCodeRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final IssueTokenService issueTokenService;

  public RecoveryMfaHandler(
      AuthCacheRepository authCacheRepository,
      MfaBackupCodeRepository mfaBackupCodeRepository,
      PasswordEncoder passwordEncoder,
      UserRepository userRepository,
      IssueTokenService issueTokenService) {
    this.authCacheRepository = authCacheRepository;
    this.mfaBackupCodeRepository = mfaBackupCodeRepository;
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.issueTokenService = issueTokenService;
  }

  @Override
  public LoginResult handle(RecoveryMfaCommand command) {
    // Lấy thông tin MFA challenge từ cache
    String cacheKey = String.format(CacheKey.MFA_CHALLENGE_PREFIX, command.challengeId());
    Optional<MfaChallengeCache> mfaChallengeCacheOpt =
        authCacheRepository.getObject(cacheKey, MfaChallengeCache.class);

    if (mfaChallengeCacheOpt.isEmpty()) {
      throw new AppException(AuthErrorCode.INVALID_VERIFICATION_CODE);
    }

    List<MfaBackupCode> backupCodes =
        mfaBackupCodeRepository.findByUserId(mfaChallengeCacheOpt.get().userId());

    Optional<MfaBackupCode> bkCode =
        backupCodes.stream()
            .filter(
                code ->
                    code.getUsedAt() == null
                        && passwordEncoder.matches(command.code(), code.getHashedCode()))
            .findAny();

    if (bkCode.isEmpty()) {
      throw new AppException(AuthErrorCode.INVALID_VERIFICATION_CODE);
    }

    bkCode.get().markAsUsed();
    mfaBackupCodeRepository.update(bkCode.get());

    authCacheRepository.delete(cacheKey);

    User user =
        userRepository
            .findById(mfaChallengeCacheOpt.get().userId())
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

    IssueTokenCommand issueTokenCommand = new IssueTokenCommand(user.getId(), user.getRoleId());
    TokenPairResult tokenPair = issueTokenService.issueToken(issueTokenCommand);

    return new LoginResult(
        tokenPair.accessToken(),
        tokenPair.refreshToken(),
        tokenPair.expiresIn(),
        tokenPair.tokenType());
  }
}
