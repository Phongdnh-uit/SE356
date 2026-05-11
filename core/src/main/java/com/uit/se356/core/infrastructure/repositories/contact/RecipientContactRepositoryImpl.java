package com.uit.se356.core.infrastructure.repositories.contact;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.utils.PageableUtil;
import com.uit.se356.core.application.contact.port.RecipientContactRepository;
import com.uit.se356.core.domain.entities.contact.RecipientContact;
import com.uit.se356.core.domain.exception.ContactErrorCode;
import com.uit.se356.core.domain.vo.area.ContactId;
import com.uit.se356.core.domain.vo.authentication.PhoneNumber;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.infrastructure.persistence.entities.contact.RecipientContactJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.contact.RecipientContactPersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.contact.RecipientContactJpaRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RecipientContactRepositoryImpl implements RecipientContactRepository {
  private final RecipientContactJpaRepository recipientContactJpaRepository;
  private final RecipientContactPersistenceMapper mapper;

  @Override
  public RecipientContact create(RecipientContact contact) {
    RecipientContactJpaEntity entity = mapper.toEntity(contact);
    RecipientContactJpaEntity savedEntity = recipientContactJpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public RecipientContact update(RecipientContact contact) {
    RecipientContactJpaEntity existingEntity =
        recipientContactJpaRepository
            .findById(contact.getId().value())
            .orElseThrow(
                () -> new AppException(ContactErrorCode.CONTACT_NOT_FOUND, contact.getId()));
    mapper.updateEntityFromDomain(contact, existingEntity);
    RecipientContactJpaEntity updatedEntity = recipientContactJpaRepository.save(existingEntity);
    return mapper.toDomain(updatedEntity);
  }

  @Override
  public PageResponse<RecipientContact> findAll(UserId ownerId, SearchPageable searchCriteria) {

    Specification<RecipientContactJpaEntity> ownerSpec =
        (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("userId"), ownerId.value());

    Specification<RecipientContactJpaEntity> finalSpec = Specification.where(ownerSpec);

    if (searchCriteria.filter() != null && !searchCriteria.filter().isBlank()) {

      Specification<RecipientContactJpaEntity> rsqlSpec =
          RSQLJPASupport.toSpecification(searchCriteria.filter());

      finalSpec = finalSpec.and(rsqlSpec);
    }

    Pageable pageable = PageableUtil.createPageable(searchCriteria);

    Page<RecipientContactJpaEntity> entityPage =
        recipientContactJpaRepository.findAll(finalSpec, pageable);

    return PageResponse.from(entityPage, mapper::toDomain);
  }

  @Override
  public Optional<RecipientContact> findByOwnerIdAndPhone(UserId ownerId, PhoneNumber phoneNumber) {
    return recipientContactJpaRepository
        .findByUserIdAndPhoneNumber(ownerId.value(), phoneNumber.value())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<RecipientContact> findById(ContactId id) {
    return recipientContactJpaRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public void delete(ContactId id) {
    recipientContactJpaRepository.deleteById(id.value());
  }

  @Override
  public boolean existsByOwnerIdAndPhone(UserId ownerId, PhoneNumber phoneNumber) {
    return recipientContactJpaRepository.existsByUserIdAndPhoneNumber(
        ownerId.value(), phoneNumber.value());
  }
}
