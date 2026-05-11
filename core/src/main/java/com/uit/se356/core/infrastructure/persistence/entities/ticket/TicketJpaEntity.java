package com.uit.se356.core.infrastructure.persistence.entities.ticket;

import com.uit.se356.common.entity.BaseEntity;
import com.uit.se356.core.domain.constants.TicketStatus;
import com.uit.se356.core.infrastructure.persistence.entities.authentication.UserJpaEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tickets")
public class TicketJpaEntity extends BaseEntity<String> {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id", nullable = false)
  private UserJpaEntity reporter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "handler_id")
  private UserJpaEntity handler;

  @Column(nullable = false)
  private String summary;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TicketStatus status;

  @Column(columnDefinition = "TEXT")
  private String resolutionNote;

  @OneToMany(
      mappedBy = "ticket",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @OrderBy("createdAt ASC")
  private List<TicketCommentJpaEntity> comments = new ArrayList<>();

  @OneToMany(
      mappedBy = "ticket",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<TicketEvidenceJpaEntity> evidences = new ArrayList<>();
}
