package com.uit.se356.core.application.authentication.handler.mfa;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.authentication.command.IssueTokenCommand;
import com.uit.se356.core.application.authentication.command.mfa.VerifyMfaCommand;
import com.uit.se356.core.application.authentication.port.in.IssueTokenService;
import com.uit.se356.core.application.authentication.port.out.AuthCacheRepository;
import com.uit.se356.core.application.authentication.port.out.MfaProvider;
import com.uit.se356.core.application.authentication.port.out.MfaRepository;
import com.uit.se356.core.application.authentication.result.LoginResult;
import com.uit.se356.core.application.authentication.result.TokenPairResult;
import com.uit.se356.core.application.authentication.result.mfa.MfaVerifyResult;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.domain.constants.CacheKey;
import com.uit.se356.core.domain.entities.authentication.Mfa;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.exception.UserErrorCode;
import com.uit.se356.core.domain.vo.authentication.MfaMethod;
import com.uit.se356.core.domain.vo.authentication.mfa.MfaChallengeCache;
import java.util.List;
import java.util.Optional;

public class VerifyMfaHandler implements CommandHandler<VerifyMfaCommand, LoginResult> {
  private final List<MfaProvider> mfaProviders;
  private final AuthCacheRepository authCacheRepository;
  private final MfaRepository mfaRepository;
  private final IssueTokenService issueTokenService;
  private final UserRepository userRepository;

  public VerifyMfaHandler(
      List<MfaProvider> mfaProviders,
      AuthCacheRepository authCacheRepository,
      MfaRepository mfaRepository,
      IssueTokenService issueTokenService,
      UserRepository userRepository) {
    this.mfaRepository = mfaRepository;
    this.mfaProviders = mfaProviders;
    this.authCacheRepository = authCacheRepository;
    this.issueTokenService = issueTokenService;
    this.userRepository = userRepository;
  }

  @Override
  public LoginResult handle(VerifyMfaCommand command) {
    // Lấy thông tin MFA challenge từ cache
    String cacheKey = String.format(CacheKey.MFA_CHALLENGE_PREFIX, command.challengeId());
    Optional<MfaChallengeCache> mfaChallengeCacheOpt =
        authCacheRepository.getObject(cacheKey, MfaChallengeCache.class);

    if (mfaChallengeCacheOpt.isEmpty()) {
      throw new AppException(AuthErrorCode.INVALID_VERIFICATION_CODE);
    }

    MfaProvider mfaProvider =
        mfaProviders.stream()
            .filter(p -> p.supports(mfaChallengeCacheOpt.get().method()))
            .findFirst()
            .orElseThrow(() -> new AppException(CommonErrorCode.INTERNAL_ERROR));

    // Lấy thông tin cấu hình MFA từ repository
    Optional<Mfa> mfaOpt =
        mfaRepository.findByUserIdAndMethod(
            mfaChallengeCacheOpt.get().userId(), mfaChallengeCacheOpt.get().method());

    if (mfaOpt.isEmpty()) {
      throw new AppException(AuthErrorCode.MFA_METHOD_NOT_FOUND);
    }

    MfaVerifyResult result = mfaProvider.verify(mfaOpt.get().getConfig(), command.credential());

    if (!result.success()) {
      throw new AppException(AuthErrorCode.INVALID_VERIFICATION_CODE);
    }
    // Nếu là WEBAUTH cần lưu lại config mới
    if (mfaOpt.get().getMethod() == MfaMethod.WEBAUTHN) {
      mfaOpt.get().updateConfig(result.updatedConfig());
      mfaRepository.update(mfaOpt.get());
    }

    // Xóa cache sau khi xác thực thành công
    authCacheRepository.delete(cacheKey);

    User user =
        userRepository
            .findById(mfaChallengeCacheOpt.get().userId())
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

    IssueTokenCommand issueTokenCommand =
        new IssueTokenCommand(mfaChallengeCacheOpt.get().userId(), user.getRoleId());
    TokenPairResult tokenPair = issueTokenService.issueToken(issueTokenCommand);
    // Trả về kết quả đăng nhập thành công
    return new LoginResult(
        tokenPair.accessToken(),
        tokenPair.refreshToken(),
        tokenPair.expiresIn(),
        tokenPair.tokenType());
  }
}
