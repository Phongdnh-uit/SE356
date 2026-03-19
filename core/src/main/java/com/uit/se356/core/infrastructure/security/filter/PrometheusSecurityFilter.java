package com.uit.se356.core.infrastructure.security.filter;

import com.uit.se356.common.dto.ErrorResponse;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.infrastructure.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PrometheusSecurityFilter extends OncePerRequestFilter {
  private final AppProperties appProperties;
  private final MessageSource messageSource;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 1. Kiểm tra xem có phải endpoint prometheus không
    if (!EndpointRequest.to("prometheus").matches(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. Lấy secret cấu hình
    String requiredSecret = appProperties.getSecurity().getMonitoringSecret();
    String authHeader = request.getHeader("Authorization");

    // 3. Logic kiểm tra Token
    boolean isValid =
        authHeader != null
            && authHeader.startsWith("Bearer ")
            && (requiredSecret != null && !requiredSecret.isBlank())
            && authHeader.substring(7).equals(requiredSecret);

    if (isValid) {
      // Gán quyền cho các Filter sau chặn lại
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(
              "grafana-monitor", null, List.of(new SimpleGrantedAuthority("ROLE_MONITORING")));
      SecurityContextHolder.getContext().setAuthentication(auth);

      // Xóa header Authroiztion để Bearer phía sau không xử lý nữa
      HttpServletRequest wrappedRequest =
          new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
              if ("Authorization".equalsIgnoreCase(name)) {
                return null;
              }
              return super.getHeader(name);
            }
          };

      filterChain.doFilter(wrappedRequest, response);
    } else {
      // 4. Trả về lỗi nếu không khớp
      sendErrorResponse(request, response);
    }
  }

  private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    ErrorResponse errorResponse =
        new ErrorResponse(
            request.getRequestURI(),
            AuthErrorCode.AUTHENTICATION_REQUIRED.getHttpStatus(),
            messageSource.getMessage(
                AuthErrorCode.AUTHENTICATION_REQUIRED.getMessageKey(),
                null,
                LocaleContextHolder.getLocale()),
            null,
            AuthErrorCode.AUTHENTICATION_REQUIRED.getCode());

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
