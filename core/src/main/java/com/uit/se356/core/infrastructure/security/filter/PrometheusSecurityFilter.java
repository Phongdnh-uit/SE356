package com.uit.se356.core.infrastructure.security.filter;

import com.uit.se356.common.dto.ErrorResponse;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.infrastructure.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
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
    boolean isPromethusEndpoint = EndpointRequest.to("prometheus").matches(request);
    if (!isPromethusEndpoint) {
      filterChain.doFilter(request, response);
      return;
    }

    // Lấy header
    String monitoringHeader = request.getHeader("X-Monitoring-Secret");
    if (monitoringHeader == null
        || appProperties.getSecurity().getMonitoringSecret() == null
        || !monitoringHeader.equals(appProperties.getSecurity().getMonitoringSecret())) {
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
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
