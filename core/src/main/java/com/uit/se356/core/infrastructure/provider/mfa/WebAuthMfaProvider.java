package com.uit.se356.core.infrastructure.provider.mfa;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.authentication.port.out.MfaProvider;
import com.uit.se356.core.application.authentication.port.out.MfaRepository;
import com.uit.se356.core.application.authentication.result.mfa.MfaChallengeResult;
import com.uit.se356.core.application.authentication.result.mfa.MfaSetupResult;
import com.uit.se356.core.application.authentication.result.mfa.MfaVerifyResult;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.domain.constants.SystemConstant;
import com.uit.se356.core.domain.entities.authentication.Mfa;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.exception.UserErrorCode;
import com.uit.se356.core.domain.vo.authentication.MfaMethod;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.mfa.MfaConfig;
import com.uit.se356.core.domain.vo.authentication.mfa.WebAuthMfaConfig;
import com.uit.se356.core.infrastructure.config.AppProperties;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.AttestationConveyancePreference;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticatorAttachment;
import com.webauthn4j.data.AuthenticatorSelectionCriteria;
import com.webauthn4j.data.AuthenticatorTransport;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialDescriptor;
import com.webauthn4j.data.PublicKeyCredentialHints;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;
import com.webauthn4j.data.PublicKeyCredentialRpEntity;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.UserVerificationRequirement;
import com.webauthn4j.data.attestation.authenticator.AAGUID;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.authenticator.COSEKey;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.verifier.exception.UserNotVerifiedException;
import com.webauthn4j.verifier.exception.VerificationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebAuthMfaProvider implements MfaProvider {

  private final ObjectMapper objectMapper;
  private final MfaRepository mfaRepository;
  private final AppProperties appProperties;
  private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();
  private final ObjectConverter objectConverter = new ObjectConverter();
  private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

  @Override
  public boolean supports(MfaMethod method) {
    return method == MfaMethod.WEBAUTHN;
  }

  @Override
  public MfaSetupResult<WebAuthMfaConfig> initiateMfaSetup(UserId userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    PublicKeyCredentialRpEntity rp =
        new PublicKeyCredentialRpEntity(
            getRpIdFromOrigin(appProperties.getFrontend().getBaseUrl()), SystemConstant.APP_NAME);
    byte[] challengeValue = generateRandomChallenge();
    Challenge challenge = new DefaultChallenge(challengeValue);
    PublicKeyCredentialUserEntity userEntity =
        new PublicKeyCredentialUserEntity(
            userId.value().getBytes(StandardCharsets.UTF_8),
            user.getFullName(),
            user.getFullName());

    List<PublicKeyCredentialParameters> pubKeyCredParams =
        List.of(
            new PublicKeyCredentialParameters(
                PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256),
            new PublicKeyCredentialParameters(
                PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256));

    AuthenticatorSelectionCriteria authenticatorSelection =
        new AuthenticatorSelectionCriteria(
            AuthenticatorAttachment.PLATFORM, false, UserVerificationRequirement.REQUIRED);

    List<PublicKeyCredentialHints> hints = List.of(PublicKeyCredentialHints.CLIENT_DEVICE);

    PublicKeyCredentialCreationOptions options =
        new PublicKeyCredentialCreationOptions(
            rp,
            userEntity,
            challenge,
            pubKeyCredParams,
            60000L, // Timeout in milliseconds (1 minute)
            Collections.emptyList(),
            authenticatorSelection,
            hints,
            AttestationConveyancePreference.NONE,
            null,
            null);
    // Cần lưu lại challenge này vào DB để hàm verify() có thể lấy lên so sánh sau này

    WebAuthMfaConfig config =
        new WebAuthMfaConfig(
            challengeValue, // challenge
            new byte[0], // credentialId chưa có
            new byte[0], // publicKey chưa có
            0, // signCount chưa có
            List.of(), // Transport chưa có
            false, // backupEligible mặc định false
            false // backupState mặc định false
            );
    Map<String, String> metadata = new HashMap<>();
    metadata.put("publicKeyCredentialCreationOptions", objectMapper.writeValueAsString(options));

    return new MfaSetupResult<>(config, metadata);
  }

  @Override
  public MfaChallengeResult initiateMfaChallenge(UserId userId, MfaMethod method) {
    // 1. Lấy danh sách các khóa đã đăng ký từ DB (Chỉ lấy các bản ghi ACTIVE)
    Optional<Mfa> mfaOpt = mfaRepository.findByUserIdAndMethod(userId, method);

    if (mfaOpt.isEmpty() || !mfaOpt.get().isVerified()) {
      throw new AppException(AuthErrorCode.MFA_METHOD_NOT_FOUND);
    }

    // 2. Tạo Challenge mới
    byte[] challengeValue = generateRandomChallenge(); // 32 bytes ngẫu nhiên
    Challenge challenge = new DefaultChallenge(challengeValue);

    WebAuthMfaConfig config = (WebAuthMfaConfig) mfaOpt.get().getConfig();
    if (config.credentialId() == null || config.credentialId().length == 0) {
      log.error("User {} has no registered credentialId for WebAuthn MFA", userId.value());
      throw new AppException(CommonErrorCode.INTERNAL_ERROR);
    }
    // 3. Chuyển đổi danh sách config từ DB thành danh sách "cho phép" (AllowCredentials)
    PublicKeyCredentialDescriptor allowCredential =
        new PublicKeyCredentialDescriptor(
            PublicKeyCredentialType.PUBLIC_KEY,
            config.credentialId(),
            config.transports().stream()
                .map(v -> AuthenticatorTransport.create(v))
                .collect(Collectors.toSet()));

    // 4. Tạo Options để gửi về Frontend (PublicKeyCredentialRequestOptions)
    // rpId phải khớp với rpId lúc đăng ký (ví dụ: "flashmile.com")
    PublicKeyCredentialRequestOptions options =
        new PublicKeyCredentialRequestOptions(
            challenge,
            60000L, // Timeout 1 phút
            getRpIdFromOrigin(appProperties.getFrontend().getBaseUrl()), // rpId
            List.of(allowCredential), // allowCredentials
            UserVerificationRequirement.REQUIRED, // Ưu tiên quét vân tay/FaceID
            List.of(), // hints
            null);

    // 5. CẬP NHẬT DB: Lưu challenge mới này vào bản ghi của User
    // Bạn cần lưu challenge này để hàm verify() có thể lấy lên so sánh
    WebAuthMfaConfig updatedConfig =
        new WebAuthMfaConfig(
            challengeValue,
            config.credentialId(),
            config.publicKeyCos(),
            config.signCount(),
            config.transports(),
            config.backupEligible(),
            config.backupState());
    mfaOpt.get().updateConfig(updatedConfig);
    mfaRepository.update(mfaOpt.get());

    return MfaChallengeResult.webAuthn(objectMapper.writeValueAsString(options));
  }

  @Override
  public MfaVerifyResult verify(MfaConfig config, String credentialJson) {
    WebAuthMfaConfig webAuthConfig = (WebAuthMfaConfig) config;
    Challenge storedChallenge = new DefaultChallenge(webAuthConfig.challenge());

    // ServerProperty theo đúng đặc tả RP ID và Origin của FlashMile
    ServerProperty serverProperty =
        ServerProperty.builder()
            .rpId(getRpIdFromOrigin(appProperties.getFrontend().getBaseUrl()))
            .challenge(storedChallenge)
            .origin(new Origin(appProperties.getFrontend().getBaseUrl()))
            .build();

    try {
      // Kiểm tra luồng dựa trên sự tồn tại của Public Key trong DB
      if (webAuthConfig.publicKeyCos() == null || webAuthConfig.publicKeyCos().length == 0) {
        // LUỒNG SETUP: Parse sang RegistrationRequest
        RegistrationData registrationRequest =
            webAuthnManager.parseRegistrationResponseJSON(credentialJson);
        return verifyRegistration(registrationRequest, serverProperty, webAuthConfig);
        // Trả về config mới
      } else {
        // LUỒNG LOGIN: Parse sang AuthenticationData
        AuthenticationData authData =
            webAuthnManager.parseAuthenticationResponseJSON(credentialJson);
        return verifyAuthentication(authData, serverProperty, webAuthConfig);
      }
    } catch (Exception e) {
      log.error("WebAuthn verification failed", e);
      return new MfaVerifyResult(false, null);
    }
  }

  // ============================ HELPERS ============================

  private byte[] generateRandomChallenge() {
    byte[] challenge = new byte[32]; // 32 bytes = 256 bits
    secureRandom.nextBytes(challenge);
    return challenge;
  }

  private MfaVerifyResult verifyRegistration(
      RegistrationData registrationData, ServerProperty serverProperty, WebAuthMfaConfig config) {
    try {
      // Sử dụng RegistrationParameters mới
      RegistrationParameters registrationParameters =
          new RegistrationParameters(
              serverProperty,
              null,
              false,
              true); // Đặt userVerificationRequired=true để ưu tiên quét vân tay/FaceID, nếu thiết
      // bị hỗ trợ UV

      // Verify bằng manager
      RegistrationData validatedData =
          webAuthnManager.verify(registrationData, registrationParameters);

      // boolean isUserVerified =
      //     validatedData.getAttestationObject().getAuthenticatorData().isFlagUV();
      // log.info("User verification result during registration: {}", isUserVerified);

      // Trích xuất thông tin để cập nhật vào bản ghi config (sau đó bạn cần save bản ghi này vào
      // DB)
      var authData = validatedData.getAttestationObject().getAuthenticatorData();
      var credentialData = authData.getAttestedCredentialData();

      // Lấy transport
      Set<AuthenticatorTransport> transports = registrationData.getTransports();
      List<String> transportList =
          (transports != null)
              ? transports.stream().map(AuthenticatorTransport::getValue).toList()
              : List.of(); // Trả về mảng rỗng thay vì null để tránh lỗi Frontend

      boolean isBackupEligible = authData.isFlagBE();
      boolean isBackupState = authData.isFlagBS();

      // Cập nhật lại Object để lớp gọi hàm này có thể lấy data lưu vào DB
      WebAuthMfaConfig updatedConfig =
          new WebAuthMfaConfig(
              config.challenge(),
              credentialData.getCredentialId(),
              objectConverter.getCborMapper().writeValueAsBytes(credentialData.getCOSEKey()),
              0, // signCount ban đầu là 0
              transportList,
              isBackupEligible,
              isBackupState);

      return new MfaVerifyResult(true, updatedConfig);
    } catch (VerificationException e) {
      if (e instanceof UserNotVerifiedException) {
        throw new AppException(AuthErrorCode.MFA_USER_NOT_VERIFIED);
      }
      return new MfaVerifyResult(false, null);
    }
  }

  private MfaVerifyResult verifyAuthentication(
      AuthenticationData authenticationData,
      ServerProperty serverProperty,
      WebAuthMfaConfig webAuthConfig) {
    try {
      // 1. Tái tạo Public Key từ DB thành đối tượng COSEKey
      COSEKey publicKey =
          objectConverter.getCborMapper().readValue(webAuthConfig.publicKeyCos(), COSEKey.class);

      // 2. Tạo CredentialRecord thay cho Authenticator
      AttestedCredentialData attestedCredentialData =
          new AttestedCredentialData(
              AAGUID.ZERO, // AAGUID có thể để ZERO nếu không có thông tin cụ thể về thiết bị
              webAuthConfig.credentialId(),
              publicKey);
      CredentialRecord credentialRecord =
          new CredentialRecordImpl(
              null, // attestationStatement
              true, // uvInitialized: true vì mình dùng TouchID/FaceID
              webAuthConfig
                  .backupEligible(), // backupEligible: mặc định false nếu không dùng Passkey đồng
              // bộ
              webAuthConfig.backupState(), // backupState: mặc định false
              webAuthConfig.signCount(), // counter (đây chính là signCount)
              attestedCredentialData, // attestedCredentialData (Must not be null)
              null, // authenticatorExtensions
              null, // clientData
              null, // clientExtensions
              Collections.emptySet() // transports (nên dùng emptySet thay vì null để an toàn)
              );

      // 3. Tạo AuthenticationParameters (Vẫn dùng Interface này nhưng truyền CredentialRecord vào)
      // Thư viện thường chấp nhận CredentialRecord ở vị trí của Authenticator do tính đa hình
      AuthenticationParameters authParams =
          new AuthenticationParameters(
              serverProperty,
              credentialRecord, // Truyền record mới vào đây
              null,
              true);

      // 4. Thực hiện verify
      webAuthnManager.verify(authenticationData, authParams);

      // 5. Kiểm tra sign count chống cloning
      long newSignCount = authenticationData.getAuthenticatorData().getSignCount();
      if (newSignCount > 0 && newSignCount <= webAuthConfig.signCount()) {
        return new MfaVerifyResult(false, null);
      }
      WebAuthMfaConfig updatedConfig =
          new WebAuthMfaConfig(
              webAuthConfig.challenge(),
              webAuthConfig.credentialId(),
              webAuthConfig.publicKeyCos(),
              newSignCount, // Cập nhật signCount mới
              webAuthConfig.transports(),
              webAuthConfig.backupEligible(),
              webAuthConfig.backupState());
      return new MfaVerifyResult(true, updatedConfig);
    } catch (VerificationException e) {
      log.error("Authentication verification failed", e);
      return new MfaVerifyResult(false, null);
    }
  }

  private String getRpIdFromOrigin(String origin) {
    // Ví dụ: Nếu origin là "https://flashmile.com/", thì rpId sẽ là "flashmile.com"
    // Bạn có thể cần xử lý thêm nếu origin có subdomain hoặc port
    try {
      URI uri = new URI(origin);
      String host = uri.getHost();
      if (host.startsWith("www.")) {
        host = host.substring(4); // Loại bỏ "www." nếu có
      }
      return host;
    } catch (URISyntaxException e) {
      log.error("Invalid origin URI: {}", origin, e);
      throw new AppException(CommonErrorCode.INTERNAL_ERROR);
    }
  }
}
