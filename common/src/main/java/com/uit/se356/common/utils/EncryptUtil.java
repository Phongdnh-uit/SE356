package com.uit.se356.common.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {

  private EncryptUtil() {
    // Private constructor to prevent instantiation
  }

  private static final String ALGORITHM = "AES/GCM/NoPadding";
  private static final int TAG_LENGTH_BIT = 128; // Độ dài mã xác thực
  private static final int IV_LENGTH_BYTE = 12; // IV chuẩn cho GCM
  private static final int AES_KEY_BIT = 256; // Dùng AES-256
  private static final SecureRandom secureRandom = new SecureRandom();

  public static String encrypt(String plainText, String secretKey, String salt) {
    try {
      // 1. Tạo IV ngẫu nhiên cho mỗi lần mã hóa (Bắt buộc để bảo mật)
      byte[] iv = new byte[IV_LENGTH_BYTE];
      secureRandom.nextBytes(iv);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
      cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(secretKey, salt), spec);

      // 2. Mã hóa
      byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

      // 3. Gộp IV và CipherText lại để lưu vào DB (cần IV để giải mã sau này)
      ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
      byteBuffer.put(iv);
      byteBuffer.put(cipherText);

      return Base64.getEncoder().encodeToString(byteBuffer.array());
    } catch (Exception e) {
      throw new RuntimeException("Encryption failed", e);
    }
  }

  public static String decrypt(String encryptedText, String secretKey, String salt) {
    try {
      byte[] decode = Base64.getDecoder().decode(encryptedText);

      // 1. Tách IV ra khỏi gói dữ liệu
      ByteBuffer byteBuffer = ByteBuffer.wrap(decode);
      byte[] iv = new byte[IV_LENGTH_BYTE];
      byteBuffer.get(iv);
      byte[] cipherText = new byte[byteBuffer.remaining()];
      byteBuffer.get(cipherText);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
      cipher.init(Cipher.DECRYPT_MODE, getSecretKey(secretKey, salt), spec);

      // 2. Giải mã
      byte[] plainText = cipher.doFinal(cipherText);
      return new String(plainText, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException("Decryption failed", e);
    }
  }

  private static SecretKey getSecretKey(String secretKey, String salt) throws Exception {

    byte[] saltByte = salt.getBytes();

    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

    KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), saltByte, 65536, 256);

    SecretKey tmp = factory.generateSecret(spec);

    return new SecretKeySpec(tmp.getEncoded(), "AES");
  }
}
