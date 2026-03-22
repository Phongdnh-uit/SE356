package com.uit.se356.core.infrastructure.security.oauth2;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.application.authentication.command.OAuth2LoginCommand;
import com.uit.se356.core.application.authentication.handler.OAuth2LoginCommandHandler;
import com.uit.se356.core.domain.constants.CacheKey;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.infrastructure.security.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
@Component
public class CustomOAuth2Service extends DefaultOAuth2UserService {
  private final OAuth2LoginCommandHandler oAuth2LoginCommandHandler;
  private final StringRedisTemplate redisTemplate;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    String provider = userRequest.getClientRegistration().getRegistrationId();
    String providerUserId = oAuth2User.getName();
    String email = oAuth2User.getAttribute("email");
    String fullName = oAuth2User.getAttribute("name");
    String verifiedPhone = getVerifiedPhoneFromSession();

    try {
      // Tạo command và gọi handler để xử lý logic đăng nhập/đăng ký
      OAuth2LoginCommand command =
          new OAuth2LoginCommand(provider, providerUserId, email, fullName, verifiedPhone);
      User user = oAuth2LoginCommandHandler.handle(command);
      // Cần gán roleId vào role của UserPrincipal để xử lý ở bước sau
      return CustomUserPrincipal.builder().id(user.getId()).role(user.getRoleId().value()).build();
    } catch (Exception ex) {
      // Nếu có lỗi trong quá trình xử lý, ném lại dưới dạng OAuth2AuthenticationException để Spring
      // Security có thể bắt và xử lý
      var throwable = new Throwable("Error during OAuth2 process", ex);
      throw new OAuth2AuthenticationException(
          new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR), throwable);
    }
  }

  private String getVerifiedPhoneFromSession() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    HttpSession session = request.getSession(false);
    if (session != null) {
      String token = (String) session.getAttribute("verificationToken");
      if (token == null || token.isBlank()) {
        return null; // Nếu không có token trong session, trả về null để tiếp tục đăng nhập bình
        // thường
      }
      session.invalidate(); // Xóa session sau khi lấy
      String key = CacheKey.PHONE_VERIFIED_PREFIX + ":" + token;
      String cachedPhone = redisTemplate.opsForValue().get(key);
      if (cachedPhone == null) {
        // Dùng wrapper để bắt lỗi
        Throwable ex = new AppException(AuthErrorCode.INVALID_VERIFICATION_CODE);
        throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR), ex);
      }
      // Xóa cache sau khi lấy
      redisTemplate.delete(key);
      return cachedPhone;
    }

    return null;
  }
}
