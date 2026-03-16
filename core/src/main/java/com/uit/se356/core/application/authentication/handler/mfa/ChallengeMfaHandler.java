package com.uit.se356.core.application.authentication.handler.mfa;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.authentication.command.mfa.MfaChallengeCommand;
import com.uit.se356.core.application.authentication.port.out.AuthCacheRepository;
import com.uit.se356.core.application.authentication.port.out.AuthConfigPort;
import com.uit.se356.core.application.authentication.port.out.MfaProvider;
import com.uit.se356.core.application.authentication.port.out.MfaRepository;
import com.uit.se356.core.application.authentication.result.mfa.MfaChallengeResult;
import com.uit.se356.core.domain.constants.CacheKey;
import com.uit.se356.core.domain.entities.authentication.Mfa;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.mfa.MfaChallengeCache;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChallengeMfaHandler
    implements CommandHandler<MfaChallengeCommand, MfaChallengeResult> {

  private final AuthCacheRepository authCacheRepository;
  private final List<MfaProvider> mfaProviders;
  private final MfaRepository mfaRepository;
  private final AuthConfigPort authConfigPort;

  public ChallengeMfaHandler(
      AuthCacheRepository authCacheRepository,
      List<MfaProvider> mfaProviders,
      MfaRepository mfaRepository,
      AuthConfigPort authConfigPort) {
    this.authCacheRepository = authCacheRepository;
    this.mfaProviders = mfaProviders;
    this.mfaRepository = mfaRepository;
    this.authConfigPort = authConfigPort;
  }

  @Override
  public MfaChallengeResult handle(MfaChallengeCommand command) {
    // Lấy userId dựa vào cache trước đó dựa vào verificationToken
    Optional<String> cachedUserIdOpt =
        authCacheRepository.get(
            String.format(CacheKey.MFA_PRECHALLENGE_PREFIX, command.verificationToken()));
    if (cachedUserIdOpt.isEmpty()) {
      throw new AppException(AuthErrorCode.INVALID_VERIFICATION_CODE);
    }

    UserId userId = new UserId(cachedUserIdOpt.get());
    // 1. Kiểm tra xem người dùng đã có cấu hình mfa chưa
    Optional<Mfa> mfaOpt = mfaRepository.findByUserIdAndMethod(userId, command.method());

    if (mfaOpt.isEmpty()) {
      throw new AppException(AuthErrorCode.MFA_METHOD_NOT_FOUND);
    }

    // Với các method khác có thể dùng từng config trong mfaOpt để check riêng
    // TODO: nghiên cứu impl vơi webauth

    MfaProvider mfaProvider =
        mfaProviders.stream()
            .filter(p -> p.supports(command.method()))
            .findFirst()
            .orElseThrow(() -> new AppException(CommonErrorCode.INTERNAL_ERROR));

    MfaChallengeResult result = mfaProvider.initiateMfaChallenge(userId, command.method());

    if (result.challengeId() == null || result.challengeId().isBlank()) {
      result = result.withChallengeId(UUID.randomUUID().toString());
    }

    String key = String.format(CacheKey.MFA_CHALLENGE_PREFIX, result.challengeId());

    MfaChallengeCache cacheData = new MfaChallengeCache(userId, command.method());

    // Lưu thông tin challenge vào cache để dùng cho bước verify sau
    authCacheRepository.setObject(
        key, cacheData, Duration.ofSeconds(authConfigPort.getMfaChallengeExpiration()));

    // Xóa cache pre-challenge vì đã chuyển sang bước challenge
    authCacheRepository.delete(String.format(CacheKey.MFA_PRECHALLENGE_PREFIX, command.verificationToken()));

    return result;
  }
}
