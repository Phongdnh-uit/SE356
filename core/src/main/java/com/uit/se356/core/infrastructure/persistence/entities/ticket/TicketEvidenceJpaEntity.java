package com.uit.se356.core.infrastructure.persistence.entities.ticket;

import com.uit.se356.common.entity.BaseEntity;
import com.uit.se356.core.infrastructure.persistence.entities.upload.FileJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ticket_evidences")
public class TicketEvidenceJpaEntity extends BaseEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ticket_id")
  private TicketJpaEntity ticket;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private TicketCommentJpaEntity comment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id", nullable = false)
  private FileJpaEntity file;
}
