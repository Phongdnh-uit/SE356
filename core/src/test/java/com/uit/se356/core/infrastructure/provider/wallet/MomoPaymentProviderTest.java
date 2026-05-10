package com.uit.se356.core.infrastructure.provider.wallet;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.uit.se356.core.infrastructure.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MomoPaymentProviderTest {

  @Mock private AppProperties appProperties;
  @Mock private AppProperties.Payment payment;
  @Mock private AppProperties.Payment.Momo momo;

  private MomoPaymentProvider momoPaymentProvider;

  @BeforeEach
  void setUp() {
    when(appProperties.getPayment()).thenReturn(payment);
    when(payment.getMomo()).thenReturn(momo);
    momoPaymentProvider = new MomoPaymentProvider(appProperties);
  }

  @Test
  void testCreatePaymentUrlWithSampleData() {
    String partnerCode = "MOMO";
    String accessKey = "F8BBA842ECF85";
    String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    String requestUrl = "https://test-payment.momo.vn";
    String returnUrl = "https://momo.vn";
    String notifyUrl = "https://momo.vn";

    lenient().when(momo.getPartnerCode()).thenReturn(partnerCode);
    lenient().when(momo.getAccessKey()).thenReturn(accessKey);
    lenient().when(momo.getSecretKey()).thenReturn(secretKey);
    lenient().when(momo.getRequestUrl()).thenReturn(requestUrl);
    lenient().when(momo.getReturnUrl()).thenReturn(returnUrl);
    lenient().when(momo.getNotifyUrl()).thenReturn(notifyUrl);

    String rawSignature =
        "partnerCode=MOMO&accessKey=F8BBA842ECF85&requestId=MM1540456472575&amount=150000&orderId=MM1540456472575&orderInfo=SDK"
            + " team.&returnUrl=https://momo.vn&notifyUrl=https://momo.vn&extraData=email=abc@gmail.com";

    String expectedSignature = "996ed81d68a1b05c99516835e404b2d0146d9b12fbcecbf80c7e51df51cac85e";

    String actualSignature = momoPaymentProvider.signHmacSHA256(rawSignature);

    assert actualSignature.equals(expectedSignature)
        : "Expected signature does not match actual signature";
  }
}
