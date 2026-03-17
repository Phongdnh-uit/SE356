package com.uit.se356.core.infrastructure.persistence.mappers.depot;

import com.uit.se356.core.domain.entities.depot.Depot;
import com.uit.se356.core.domain.vo.area.Coordinate;
import com.uit.se356.core.domain.vo.depot.DepotId;
import com.uit.se356.core.infrastructure.persistence.entities.depot.DepotJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DepotPersistenceMapper {
  public Depot toDomain(DepotJpaEntity entity) {
    if (entity == null) return null;
    return Depot.rehydrate(
        new DepotId(entity.getId()),
        entity.getName(),
        entity.getType(),
        new Coordinate(entity.getLat(), entity.getLng()),
        entity.isStartNode());
  }

  public DepotJpaEntity toEntity(Depot domain) {
    if (domain == null) return null;
    DepotJpaEntity entity = new DepotJpaEntity();
    entity.setId(domain.getId().value());
    entity.setName(domain.getName());
    entity.setType(domain.getType());
    entity.setLat(domain.getCoordinate().latitude());
    entity.setLng(domain.getCoordinate().longitude());
    entity.setStartNode(domain.isStartNode());
    return entity;
  }

  public void updateEntityFromDomain(Depot domain, DepotJpaEntity entity) {
    entity.setName(domain.getName());
    entity.setType(domain.getType());
    entity.setLat(domain.getCoordinate().latitude());
    entity.setLng(domain.getCoordinate().longitude());
    entity.setStartNode(domain.isStartNode());
  }
}
