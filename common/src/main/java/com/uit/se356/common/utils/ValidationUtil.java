package com.uit.se356.common.utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.util.Collections;
import java.util.Map;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public class ValidationUtil {
  private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

  private ValidationUtil() {
    // Chặn khởi tạo đối tượng
  }

  public static boolean isValidPhoneNumber(String phoneNumber, String regionCode) {
    try {
      var numberProto = phoneUtil.parse(phoneNumber, regionCode);
      return phoneUtil.isValidNumber(numberProto);
    } catch (Exception e) {
      return false;
    }
  }

  public static String formatPhoneToE164(String phoneNumber, String regionCode) {
    try {
      var numberProto = phoneUtil.parse(phoneNumber, regionCode);
      return phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
    } catch (Exception e) {
      return null;
    }
  }

  public static Map<String, Object> parseCredentialJson(String credentialJson) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(credentialJson, new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      return Collections.emptyMap();
    }
  }
}
