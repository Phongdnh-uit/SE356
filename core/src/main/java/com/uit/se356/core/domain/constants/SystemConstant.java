package com.uit.se356.core.domain.constants;

public interface SystemConstant {
  String OAUTH2_BASE_URI = "/oauth2";
  String OAUTH2_AUTHORIZATION_BASE_URI = OAUTH2_BASE_URI + "/authorize";
  String OAUTH2_AUTHORIZATION_CALLBACK_URI = OAUTH2_BASE_URI + "/callback/{registrationId}";
  String APP_NAME = "FlashMile";
}
