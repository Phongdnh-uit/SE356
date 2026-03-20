package com.uit.se356.core.infrastructure.persistence.entities.authentication;

import com.uit.se356.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "permissions",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"resource", "action"})})
public class PermissionJpaEntity extends BaseEntity<String> {

  @Column(nullable = false)
  private String name;

  private String description;

  @Column(nullable = false)
  private String resource;

  @Column(nullable = false)
  private String action;

  @Column(name = "expression")
  private String condition;

  @ManyToMany(mappedBy = "permissions")
  private Set<RoleJpaEntity> roles = new HashSet<>();
}
