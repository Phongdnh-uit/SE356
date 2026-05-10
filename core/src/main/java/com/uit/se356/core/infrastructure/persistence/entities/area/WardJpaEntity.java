package com.uit.se356.core.infrastructure.persistence.entities.area;

import com.uit.se356.common.entity.BaseEntity;
import com.uit.se356.core.domain.vo.area.WardType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(
    name = "wards",
    indexes = {
      @Index(name = "idx_wards_code", columnList = "code"),
      @Index(name = "idx_wards_name", columnList = "name"),
      @Index(name = "idx_wards_province_id", columnList = "province_id")
    })
public class WardJpaEntity extends BaseEntity<String> {

  @Column(name = "code", nullable = false, unique = true, length = 50)
  private String code;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "province_id", nullable = false, length = 36)
  private String provinceId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 50)
  private WardType type;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "polygon", columnDefinition = "jsonb")
  private String polygon;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "province_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private ProvinceJpaEntity province;
}
