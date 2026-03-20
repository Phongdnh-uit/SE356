package com.uit.se356.core.infrastructure.repositories.authentication;

import com.uit.se356.common.dto.SearchRequest;
import com.uit.se356.common.utils.PageableUtil;
import com.uit.se356.core.application.authentication.port.out.PermissionRepository;
import com.uit.se356.core.application.authentication.projections.PermissionSummaryProjection;
import com.uit.se356.core.domain.entities.authentication.Permission;
import com.uit.se356.core.domain.vo.authentication.PermissionId;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import com.uit.se356.core.infrastructure.persistence.entities.authentication.PermissionJpaEntity;
import com.uit.se356.core.infrastructure.persistence.entities.authentication.RoleJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.authentication.PermissionPersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.authentication.PermissionJpaRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class PermissionRepositoryImpl implements PermissionRepository {
  private final PermissionJpaRepository permissionJpaRepository;
  private final PermissionPersistenceMapper permissionPersistenceMapper;

  @Override
  @Transactional
  public Permission create(Permission newPermission) {
    PermissionJpaEntity entityToCreate = permissionPersistenceMapper.toEntity(newPermission);
    PermissionJpaEntity savedEntity = permissionJpaRepository.save(entityToCreate);
    return permissionPersistenceMapper.toDomain(savedEntity);
  }

  @Override
  @Transactional
  public Permission update(Permission permissionToUpdate) {
    // 1. Lấy entity hiện tại
    PermissionJpaEntity existingEntity =
        permissionJpaRepository
            .findById(permissionToUpdate.getId().value())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Permission not found with id: " + permissionToUpdate.getId().value()));
    // Hard-code exception do đã validate ở services layer rồi, nếu
    // không tìm thấy thì có thể throw exception hoặc handle
    // theo cách khác tùy nhu cầu

    // 2. Cập nhật entity với dữ liệu mới từ domain object
    permissionPersistenceMapper.updateFromDomain(permissionToUpdate, existingEntity);

    // 3. Không cần save vì @Transactional sẽ tự động flush các thay đổi khi transaction kết thúc
    return permissionPersistenceMapper.toDomain(existingEntity);
  }

  @Override
  public boolean existsByCode(String code) {
    return permissionJpaRepository.exists(
        (root, query, criteriaBuilder) -> {
          return criteriaBuilder.equal(root.get("code"), code);
        });
  }

  @Override
  public List<PermissionSummaryProjection> findAllByRoleId(RoleId roleId) {
    Specification<PermissionJpaEntity> spec =
        (root, query, builder) -> {
          Join<PermissionJpaEntity, RoleJpaEntity> roleJoin = root.join("roles");
          return builder.equal(roleJoin.get("id"), roleId.value());
        };
    return permissionJpaRepository.findBy(spec, q -> q.as(PermissionSummaryProjection.class).all());
  }

  @Override
  public List<PermissionSummaryProjection> findAll(SearchRequest searchPageable) {
    Specification<PermissionJpaEntity> spec =
        RSQLJPASupport.toSpecification(searchPageable.filter());
    Pageable pageable = PageableUtil.createPageable(searchPageable);
    var page =
        permissionJpaRepository.findBy(
            spec, q -> q.as(PermissionSummaryProjection.class).page(pageable));
    return page.getContent();
  }

  @Override
  public Set<PermissionId> findExistingIds(Set<PermissionId> permissionIds) {
    var existingIds =
        permissionJpaRepository.findExistingIds(
            permissionIds.stream().map(PermissionId::value).collect(Collectors.toSet()));
    return existingIds.stream().map(PermissionId::new).collect(Collectors.toSet());
  }

  @Override
  public List<PermissionSummaryProjection> findAllByRoleId(
      RoleId roleId, SearchRequest searchRequest) {
    Specification<PermissionJpaEntity> spec =
        (root, query, builder) -> {
          Join<PermissionJpaEntity, RoleJpaEntity> roleJoin = root.join("roles");
          return builder.equal(roleJoin.get("id"), roleId.value());
        };
    spec = spec.and(RSQLJPASupport.toSpecification(searchRequest.filter()));
    Pageable pageable = PageableUtil.createPageable(searchRequest);
    var page =
        permissionJpaRepository.findBy(
            spec, q -> q.as(PermissionSummaryProjection.class).page(pageable));
    return page.getContent();
  }

  @Override
  public void deleteAllById(Set<PermissionId> permissionIds) {
    permissionJpaRepository.deleteAllById(
        permissionIds.stream().map(PermissionId::value).collect(Collectors.toSet()));
  }

  @Override
  public List<Permission> findAll() {
    List<PermissionJpaEntity> entities = permissionJpaRepository.findAll();
    return entities.stream()
        .map(permissionPersistenceMapper::toDomain)
        .collect(Collectors.toList());
  }
}
