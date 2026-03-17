package com.uit.se356.core.infrastructure.persistence.mappers.authentication;

import com.uit.se356.core.domain.entities.authentication.Mfa;
import com.uit.se356.core.domain.vo.authentication.MfaId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.mfa.EmailMfaConfig;
import com.uit.se356.core.domain.vo.authentication.mfa.MfaConfig;
import com.uit.se356.core.domain.vo.authentication.mfa.TotpMfaConfig;
import com.uit.se356.core.domain.vo.authentication.mfa.WebAuthMfaConfig;
import com.uit.se356.core.infrastructure.persistence.entities.authentication.MultifactorJpaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class MfaPersistenceMapper {

  private final ObjectMapper objectMapper;

  public Mfa toDomain(MultifactorJpaEntity entity) {
    if (entity == null) {
      return null;
    }

    // Dựa vào method để xác định loại MfaConfig nào cần được deserialize
    MfaConfig mfaConfig =
        switch (entity.getMethod()) {
          case TOTP -> objectMapper.readValue(entity.getDetails(), TotpMfaConfig.class);
          case EMAIL -> objectMapper.readValue(entity.getDetails(), EmailMfaConfig.class);
          case WEBAUTHN -> objectMapper.readValue(entity.getDetails(), WebAuthMfaConfig.class);
        };

    return Mfa.rehydrate(
        new MfaId(entity.getId()),
        new UserId(entity.getUserId()),
        entity.getMethod(),
        mfaConfig,
        entity.isVerified());
  }

  public MultifactorJpaEntity toEntity(Mfa domain) {
    if (domain == null) {
      return null;
    }

    String details = objectMapper.writeValueAsString(domain.getConfig());

    MultifactorJpaEntity entity = new MultifactorJpaEntity();
    entity.setId(domain.getId().value());
    entity.setUserId(domain.getUserId().value());
    entity.setMethod(domain.getMethod());
    entity.setDetails(details);
    entity.setVerified(domain.isVerified());
    return entity;
  }

  public void updateEntityFromDomain(Mfa domain, MultifactorJpaEntity existingEntity) {
    String details = objectMapper.writeValueAsString(domain.getConfig());

    existingEntity.setMethod(domain.getMethod());
    existingEntity.setDetails(details);
    existingEntity.setVerified(domain.isVerified());
  }
}
