package com.uit.se356.core.infrastructure.persistence.entities.ticket;

import com.uit.se356.common.entity.BaseEntity;
import com.uit.se356.core.infrastructure.persistence.entities.authentication.UserJpaEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ticket_comments")
public class TicketCommentJpaEntity extends BaseEntity<String> {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ticket_id", nullable = false)
  private TicketJpaEntity ticket;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private UserJpaEntity author;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @OneToMany(
      mappedBy = "comment",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<TicketEvidenceJpaEntity> evidences = new ArrayList<>();
}
