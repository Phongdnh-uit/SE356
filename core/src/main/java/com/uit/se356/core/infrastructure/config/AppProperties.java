package com.uit.se356.core.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application")
public class AppProperties {

  private Security security;
  private Mail mail;
  private Frontend frontend;
  private Verification verification;
  private Bootstrap bootstrap;
  private S3 s3;

  @Getter
  @Setter
  public static class Verification {
    private long emailLinkExpiration;
    private long smsOtpExpiration;
    private long phoneVerifiedTokenExpiration;
    private long forgotPasswordCodeExpiration;
    private long prechallengeTokenExpiration;
    private long mfaChallengeExpiration;
  }

  @Getter
  @Setter
  public static class Security {
    private Jwt jwt;
    private String encryptionSecretKey;
    private String encryptionSalt;
  }

  @Getter
  @Setter
  public static class Jwt {
    private String secretKey;
    private long expiration;
    private long refreshExpiration;
  }

  @Getter
  @Setter
  public static class Mail {
    private String from;
  }

  @Getter
  @Setter
  public static class Frontend {
    private String baseUrl;
    private String verifyEmailPath;
    private String resetPasswordPath;
    private String logoUrl;
    private String oauth2CallbackPath;
  }

  @Getter
  @Setter
  public static class Bootstrap {
    private String adminFullName;
    private String adminEmail;
    private String adminPhoneNumber;
    private String adminPassword;
  }

  @Getter
  @Setter
  public static class S3 {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String mainBucketName;
    private String quarantineBucketName;
  }
}
