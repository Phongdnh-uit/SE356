package com.uit.se356.core.application.wallet.strategies;

import com.uit.se356.core.application.wallet.result.PaymentCallbackResult;
import com.uit.se356.core.domain.entities.wallet.WalletTransaction;
import com.uit.se356.core.domain.vo.wallet.PaymentProvider;
import java.util.Map;

/**
 * Interface định nghĩa chiến lược (Strategy) cho các nhà cung cấp thanh toán (Payment Provider).
 * Giúp trừu tượng hóa logic cụ thể của từng bên (VNPay, MoMo, ...) ra khỏi luồng nghiệp vụ chính.
 */
public interface PaymentProviderStrategy {

  /** Kiểm tra xem strategy này có hỗ trợ provider tương ứng hay không. */
  boolean supports(PaymentProvider provider);

  /**
   * Tạo Payment URL từ thông tin giao dịch.
   *
   * @param transaction Thông tin giao dịch đang ở trạng thái PENDING.
   * @return URL để người dùng thực hiện thanh toán phía Provider.
   */
  String createPaymentUrl(WalletTransaction transaction);

  /**
   * Xác thực chữ ký (Signature/Hash) từ callback của Provider.
   *
   * @param params Toàn bộ parameters nhận được từ callback.
   * @return true nếu chữ ký hợp lệ.
   */
  boolean verifyCallback(Map<String, Object> params);

  /**
   * Parse dữ liệu từ callback của Provider sang định dạng chuẩn của hệ thống.
   *
   * @param params Toàn bộ parameters nhận được từ callback.
   * @return Kết quả đã parse.
   */
  PaymentCallbackResult parseCallback(Map<String, Object> params);
}
