package com.uit.se356.core.infrastructure.provider.wallet;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.wallet.result.PaymentCallbackResult;
import com.uit.se356.core.application.wallet.strategies.PaymentProviderStrategy;
import com.uit.se356.core.domain.entities.wallet.WalletTransaction;
import com.uit.se356.core.domain.vo.wallet.PaymentProvider;
import com.uit.se356.core.infrastructure.config.AppProperties;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class MomoPaymentProvider implements PaymentProviderStrategy {

  private final AppProperties appProperties;
  private static final String HMAC_SHA256 = "HmacSHA256";

  @Override
  public boolean supports(PaymentProvider provider) {
    return provider == PaymentProvider.MOMO;
  }

  @Override
  public String createPaymentUrl(WalletTransaction transaction) {
    String endpoint =
        appProperties.getPayment().getMomo().getRequestUrl() + "/v2/gateway/api/create";
    String partnerCode = appProperties.getPayment().getMomo().getPartnerCode();

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("partnerCode", partnerCode);
    requestBody.put("requestId", transaction.getId().toString());
    long amountInVND = transaction.getAmount().longValue(); // Momo yêu cầu amount là số nguyên
    requestBody.put("amount", amountInVND);
    requestBody.put("orderId", transaction.getId().toString());
    requestBody.put("orderInfo", "Payment for wallet top-up");
    requestBody.put("redirectUrl", appProperties.getPayment().getMomo().getReturnUrl());
    requestBody.put("ipnUrl", appProperties.getPayment().getMomo().getNotifyUrl());
    requestBody.put("requestType", "captureWallet");
    requestBody.put("extraData", "");
    requestBody.put("lang", "en");

    log.info("Creating Momo payment with request: {}", requestBody);

    String singature = generateSignature(requestBody);
    requestBody.put("signature", singature);

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    try {
      @SuppressWarnings("rawtypes")
      ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);
      @SuppressWarnings("unchecked")
      Map<String, Object> responseBody = response.getBody();

      if (response.getStatusCode().is2xxSuccessful() && responseBody != null) {
        // 1. Kiểm tra mã kết quả (0 là thành công)
        int resultCode = Integer.parseInt(responseBody.get("resultCode").toString());
        if (resultCode != 0) {
          log.error("Momo error: {} - {}", resultCode, responseBody.get("message"));
          throw new AppException(
              CommonErrorCode.INTERNAL_ERROR, "Momo payment error: " + responseBody.get("message"));
        }

        // 2. So sánh dữ liệu cốt lõi (Data Integrity)
        if (!requestBody.get("amount").toString().equals(responseBody.get("amount").toString())
            || !requestBody
                .get("orderId")
                .toString()
                .equals(responseBody.get("orderId").toString())) {
          log.error("Data mismatch! Potential tampering.");
          throw new AppException(CommonErrorCode.INTERNAL_ERROR, "Data integrity check failed");
        }

        return responseBody.get("payUrl").toString();
      } else {
        log.error(
            "Failed to create Momo payment. Status: {}, Body: {}",
            response.getStatusCode(),
            responseBody);
        throw new AppException(CommonErrorCode.INTERNAL_ERROR, "Failed to create Momo payment");
      }
    } catch (Exception e) {
      throw new AppException(CommonErrorCode.INTERNAL_ERROR, "Failed to create Momo payment");
    }
  }

  @Override
  public boolean verifyCallback(Map<String, Object> params) {
    return verifyCallbackSignature(params);
  }

  @Override
  public PaymentCallbackResult parseCallback(Map<String, Object> params) {
    String transactionId = params.get("orderId").toString();
    boolean success = "0".equals(params.get("resultCode").toString());
    String providerReferenceId = params.get("transId").toString();
    String rawResponse = params.toString();
    // Parse hết các trường còn lại vào metadata để lưu trữ (nếu cần)
    return new PaymentCallbackResult(
        transactionId, success, providerReferenceId, rawResponse, params);
  }

  // ============================ HELPER ============================
  private boolean verifyCallbackSignature(Map<String, Object> params) {
    String accessKey = appProperties.getPayment().getMomo().getAccessKey();
    String data =
        "accessKey="
            + accessKey
            + "&amount="
            + params.get("amount")
            + "&extraData="
            + params.get("extraData")
            + "&message="
            + params.get("message")
            + "&orderId="
            + params.get("orderId")
            + "&orderInfo="
            + params.get("orderInfo")
            + "&orderType="
            + params.get("orderType")
            + "&partnerCode="
            + params.get("partnerCode")
            + "&payType="
            + params.get("payType")
            + "&requestId="
            + params.get("requestId")
            + "&responseTime="
            + params.get("responseTime")
            + "&resultCode="
            + params.get("resultCode")
            + "&transId="
            + params.get("transId");

    String expectedSignature = signHmacSHA256(data);
    String providedSignature = params.get("signature").toString();
    return expectedSignature.equals(providedSignature);
  }

  private String generateSignature(Map<String, Object> params) {
    String accessKey = appProperties.getPayment().getMomo().getAccessKey();
    String data =
        "accessKey="
            + accessKey
            + "&amount="
            + params.get("amount")
            + "&extraData="
            + params.get("extraData")
            + "&ipnUrl="
            + params.get("ipnUrl")
            + "&orderId="
            + params.get("orderId")
            + "&orderInfo="
            + params.get("orderInfo")
            + "&partnerCode="
            + params.get("partnerCode")
            + "&redirectUrl="
            + params.get("redirectUrl")
            + "&requestId="
            + params.get("requestId")
            + "&requestType="
            + params.get("requestType");
    return signHmacSHA256(data);
  }

  public String signHmacSHA256(String data) {
    try {
      SecretKeySpec signingKey =
          new SecretKeySpec(
              appProperties.getPayment().getMomo().getSecretKey().getBytes(StandardCharsets.UTF_8),
              HMAC_SHA256);
      Mac mac = Mac.getInstance(HMAC_SHA256);
      mac.init(signingKey);

      byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return toHexString(rawHmac);
    } catch (Exception e) {
      throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
    }
  }

  private static String toHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 2);
    try (Formatter formatter = new Formatter(sb)) {
      for (byte b : bytes) {
        formatter.format("%02x", b);
      }
    }
    return sb.toString();
  }
}
