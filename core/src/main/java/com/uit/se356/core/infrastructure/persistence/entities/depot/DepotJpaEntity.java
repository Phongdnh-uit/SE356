package com.uit.se356.core.infrastructure.persistence.entities.depot;

import com.uit.se356.common.entity.BaseEntity;
import com.uit.se356.core.domain.vo.depot.DepotType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "depots",
    indexes = {
      @Index(name = "idx_depots_name", columnList = "name"),
      @Index(name = "idx_depots_type", columnList = "type"),
      @Index(name = "idx_depots_is_start_node", columnList = "is_start_node"),
      @Index(name = "idx_depots_created_at", columnList = "created_at")
    })
public class DepotJpaEntity extends BaseEntity<String> {
  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private DepotType type;

  @Column(nullable = false)
  private Double lat;

  @Column(nullable = false)
  private Double lng;

  @Column(name = "is_start_node", nullable = false)
  private boolean startNode;
}
