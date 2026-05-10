package com.uit.se356.core.infrastructure.persistence.repositories.authentication;

import com.uit.se356.common.repository.CommonRepository;
import com.uit.se356.core.application.user.projections.UserSummaryProjection;
import com.uit.se356.core.domain.vo.authentication.UserStatus;
import com.uit.se356.core.infrastructure.persistence.entities.authentication.UserJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends CommonRepository<UserJpaEntity, String> {

  @EntityGraph(attributePaths = "role")
  Optional<UserJpaEntity> findByEmail(String email);

  @EntityGraph(attributePaths = "role")
  Optional<UserJpaEntity> findByPhoneNumber(String phoneNumber);

  @EntityGraph(attributePaths = "role")
  @Query("SELECT u FROM UserJpaEntity u WHERE u.status = :status")
  List<UserJpaEntity> findByStatus(@Param("status") UserStatus status);

  boolean existsByEmail(String email);

  boolean existsByPhoneNumber(String phoneNumber);

  /** Tìm tất cả User kèm Role (JOIN FETCH) để tránh N+1 khi map sang domain. */
  @Query("SELECT u FROM UserJpaEntity u JOIN FETCH u.role")
  List<UserJpaEntity> findAllWithRole();

  /** Tìm User theo ID kèm Role (JOIN FETCH) để tránh lazy load khi map sang domain. */
  @EntityGraph(attributePaths = "role")
  @Query("SELECT u FROM UserJpaEntity u WHERE u.id = :id")
  Optional<UserJpaEntity> findByIdWithRole(@Param("id") String id);

  /** Phân trang: Tìm tất cả User kèm Role. */
  @EntityGraph(attributePaths = "role")
  @Query("SELECT u FROM UserJpaEntity u")
  Page<UserJpaEntity> findAllWithRole(Pageable pageable);

  /** Phân trang: Tìm User theo status kèm Role. */
  @EntityGraph(attributePaths = "role")
  @Query("SELECT u FROM UserJpaEntity u WHERE u.status = :status")
  Page<UserJpaEntity> findByStatus(@Param("status") UserStatus status, Pageable pageable);

  @Query("SELECT u FROM UserJpaEntity u JOIN FETCH u.role WHERE u.role.id = :roleId")
  List<UserJpaEntity> findByRoleId(@Param("roleId") String value);

  @Query(
"""
SELECT new com.uit.se356.core.application.user.projections.UserSummaryProjection(
    u.id,
    u.fullName,
    r.name,
    u.phoneNumber,
    u.email,
    u.status
)
FROM UserJpaEntity u
JOIN u.role r
""")
  Page<UserSummaryProjection> findAllProjection(Pageable pageable);

  @Query(
"""
SELECT new com.uit.se356.core.application.user.projections.UserSummaryProjection(
    u.id,
    u.fullName,
    r.name,
    u.phoneNumber,
    u.email,
    u.status
)
FROM UserJpaEntity u
JOIN u.role r
WHERE u.status = :status
""")
  Page<UserSummaryProjection> findByStatusWithRoleProjection(
      @Param("status") UserStatus status, Pageable pageable);
}
