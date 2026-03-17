package com.uit.se356.core.infrastructure.provider.mfa;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.authentication.port.out.MfaProvider;
import com.uit.se356.core.application.authentication.result.mfa.MfaChallengeResult;
import com.uit.se356.core.application.authentication.result.mfa.MfaSetupResult;
import com.uit.se356.core.application.authentication.result.mfa.MfaVerifyResult;
import com.uit.se356.core.domain.vo.authentication.MfaMethod;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.mfa.MfaConfig;
import com.uit.se356.core.domain.vo.authentication.mfa.TotpMfaConfig;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TotpMfaProvider implements MfaProvider {

  @Override
  public boolean supports(MfaMethod method) {
    return method == MfaMethod.TOTP;
  }

  @Override
  public MfaSetupResult<TotpMfaConfig> initiateMfaSetup(UserId userId) {
    String secretKey = generateKey();
    TotpMfaConfig config = new TotpMfaConfig(secretKey);
    String qrCodeUrl = generateQRUrl(secretKey, userId.value());
    Map<String, String> metadata = new HashMap<>();
    metadata.put("qrCodeUrl", qrCodeUrl);
    metadata.put("secretKey", secretKey);
    return new MfaSetupResult<>(config, metadata);
  }

  @Override
  public MfaChallengeResult initiateMfaChallenge(UserId userId, MfaMethod method) {
    // TOTP does not require a challenge to be generated, so we return an empty result
    return MfaChallengeResult.totp();
  }

  @Override
  public MfaVerifyResult verify(MfaConfig config, String credential) {
    if (!(config instanceof TotpMfaConfig)) {
      log.error(
          "Invalid MFA config type: expected TotpMfaConfig, got {}", config.getClass().getName());
      throw new AppException(CommonErrorCode.INTERNAL_ERROR);
    }
    TotpMfaConfig totpConfig = (TotpMfaConfig) config;
    try {
      int code = Integer.parseInt(credential);
      return new MfaVerifyResult(isValid(totpConfig.secretKey(), code), null);
    } catch (NumberFormatException e) {
      log.warn("Invalid TOTP code format: {}", credential);
      return new MfaVerifyResult(false, null);
    }
  }

  // ============================ HELPERS ============================

  private boolean isValid(String secret, Integer code) {
    GoogleAuthenticator gAuth =
        new GoogleAuthenticator(
            new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().build());
    return gAuth.authorize(secret, code);
  }

  private String generateKey() {
    GoogleAuthenticator gAuth = new GoogleAuthenticator();
    final GoogleAuthenticatorKey key = gAuth.createCredentials();
    return key.getKey();
  }

  private String generateQRUrl(String secret, String username) {
    String url =
        GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
            "FlashMile", username, new GoogleAuthenticatorKey.Builder(secret).build());
    try {
      return generateQRBase64(url);
    } catch (Exception e) {
      log.error("Failed to generate QR code URL", e);
      throw new AppException(CommonErrorCode.INTERNAL_ERROR);
    }
  }

  private String generateQRBase64(String qrCodeText) {
    try {
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      Map<EncodeHintType, Object> hints = new HashMap<>();
      hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
      BitMatrix bitMatrix =
          qrCodeWriter.encode(qrCodeText, com.google.zxing.BarcodeFormat.QR_CODE, 200, 200, hints);
      BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "jpeg", baos);
      byte[] imageBytes = baos.toByteArray();
      baos.close();
      String qrImage = Base64.getEncoder().encodeToString(imageBytes);
      return "data:image/jpeg;base64," + qrImage;
    } catch (WriterException | IOException e) {
      log.error("Failed to generate QR code", e);
      throw new AppException(CommonErrorCode.INTERNAL_ERROR);
    }
  }
}
