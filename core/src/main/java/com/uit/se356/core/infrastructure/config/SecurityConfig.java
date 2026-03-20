package com.uit.se356.core.infrastructure.config;

import com.uit.se356.core.domain.constants.SecurityConstant;
import com.uit.se356.core.domain.constants.SystemConstant;
import com.uit.se356.core.infrastructure.security.CustomAuthEntryPoint;
import com.uit.se356.core.infrastructure.security.filter.PrometheusSecurityFilter;
import com.uit.se356.core.infrastructure.security.jwt.CustomJwtConverter;
import com.uit.se356.core.infrastructure.security.oauth2.CustomOAuth2AuthRequestResolver;
import com.uit.se356.core.infrastructure.security.oauth2.CustomOAuth2Service;
import com.uit.se356.core.infrastructure.security.oauth2.OAuth2FailureHandler;
import com.uit.se356.core.infrastructure.security.oauth2.OAuth2SuccessHandler;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(2)
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      CustomJwtConverter customJwtConverter,
      CustomAuthEntryPoint customAuthEntryPoint,
      PrometheusSecurityFilter prometheusSecurityFilter)
      throws Exception {
    http.addFilterBefore(prometheusSecurityFilter, BearerTokenAuthenticationFilter.class)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                    .permitAll()
                    .requestMatchers(EndpointRequest.to("health", "info", "prometheus"))
                    .permitAll()
                    .requestMatchers(SecurityConstant.PUBLIC_URLS)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtConverter)))
        .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthEntryPoint));

    return http.build();
  }

  @Bean
  @Order(1)
  SecurityFilterChain oauth2LoginChain(
      HttpSecurity http,
      OAuth2SuccessHandler oAuth2SuccessHandler,
      OAuth2FailureHandler oAuth2FailureHandler,
      CustomOAuth2Service oAuth2UserService,
      CustomOAuth2AuthRequestResolver authorizationRequestResolver,
      CustomAuthEntryPoint customAuthEntryPoint)
      throws Exception {
    http.securityMatcher(
            SystemConstant.OAUTH2_AUTHORIZATION_BASE_URI + "/**",
            SystemConstant.OAUTH2_AUTHORIZATION_CALLBACK_URI + "/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .formLogin(AbstractHttpConfigurer::disable)
        .oauth2Login(
            oauth2 ->
                oauth2
                    .authorizationEndpoint(
                        endpoint ->
                            endpoint
                                .baseUri(SystemConstant.OAUTH2_AUTHORIZATION_BASE_URI)
                                .authorizationRequestResolver(authorizationRequestResolver))
                    .redirectionEndpoint(
                        endpoint ->
                            endpoint.baseUri(SystemConstant.OAUTH2_AUTHORIZATION_CALLBACK_URI))
                    .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler(oAuth2FailureHandler))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthEntryPoint));
    return http.build();
  }

  @Bean
  JwtDecoder jwtDecoder() {
    SecretKey originalKey = new SecretKeySpec(secretKey.getBytes(), JWT_ALGORITHM.getName());
    return NimbusJwtDecoder.withSecretKey(originalKey).build();
  }
}
