package com.uit.se356.core.infrastructure.persistence.repositories.depot;

import com.uit.se356.common.repository.CommonRepository;
import com.uit.se356.core.infrastructure.persistence.entities.depot.DepotJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DepotJpaRepository extends CommonRepository<DepotJpaEntity, String> {
  // Công thức Haversine bằng Native SQL
  @Query(
      value =
          """
      SELECT COUNT(*) > 0 FROM depots d
      WHERE (:excludeId IS NULL OR d.id != :excludeId)
      AND (
        6371 * acos(
          cos(radians(:lat)) * cos(radians(d.lat)) * cos(radians(d.lng) - radians(:lng))
          + sin(radians(:lat)) * sin(radians(d.lat))
        )
      ) <= :radiusInKm
      """,
      nativeQuery = true)
  boolean existsDepotWithinRadius(
      @Param("lat") double lat,
      @Param("lng") double lng,
      @Param("radiusInKm") double radiusInKm,
      @Param("excludeId") String excludeId);
}
