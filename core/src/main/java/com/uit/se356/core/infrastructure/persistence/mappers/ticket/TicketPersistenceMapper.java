package com.uit.se356.core.infrastructure.persistence.mappers.ticket;

import com.uit.se356.core.domain.entities.ticket.Ticket;
import com.uit.se356.core.domain.entities.ticket.TicketComment;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.ticket.TicketId;
import com.uit.se356.core.domain.vo.upload.FileId;
import com.uit.se356.core.infrastructure.persistence.entities.authentication.UserJpaEntity;
import com.uit.se356.core.infrastructure.persistence.entities.ticket.TicketEvidenceJpaEntity;
import com.uit.se356.core.infrastructure.persistence.entities.ticket.TicketJpaEntity;
import com.uit.se356.core.infrastructure.persistence.entities.upload.FileJpaEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TicketPersistenceMapper {

  public Ticket toDomain(TicketJpaEntity entity) {
    if (entity == null) {
      return null;
    }

    List<TicketComment> comments =
        entity.getComments().stream()
            .map(
                commentEntity ->
                    TicketComment.rehydrate(
                        commentEntity.getId(),
                        new UserId(commentEntity.getAuthor().getId()),
                        commentEntity.getContent(),
                        commentEntity.getEvidences().stream()
                            .map(e -> new FileId(e.getFile().getId()))
                            .toList(),
                        commentEntity.getCreatedAt()))
            .toList();

    List<FileId> ticketEvidences =
        entity.getEvidences().stream()
            .filter(e -> e.getComment() == null)
            .map(e -> new FileId(e.getFile().getId()))
            .toList();

    return Ticket.rehydrate(
        new TicketId(entity.getId()),
        new UserId(entity.getReporter().getId()),
        entity.getHandler() != null ? new UserId(entity.getHandler().getId()) : null,
        entity.getSummary(),
        entity.getDescription(),
        entity.getStatus(),
        entity.getResolutionNote(),
        ticketEvidences,
        new ArrayList<>(comments));
  }

  public TicketJpaEntity toEntity(
      Ticket domain,
      UserJpaEntity reporter,
      UserJpaEntity handler,
      List<FileJpaEntity> evidenceFiles) {
    TicketJpaEntity entity = new TicketJpaEntity();
    entity.setId(domain.getId().value());
    entity.setReporter(reporter);
    entity.setHandler(handler);
    entity.setSummary(domain.getSummary());
    entity.setDescription(domain.getDescription());
    entity.setStatus(domain.getStatus());
    entity.setResolutionNote(domain.getResolutionNote());

    // Evidences for ticket
    List<TicketEvidenceJpaEntity> evidenceEntities =
        evidenceFiles.stream()
            .map(
                f -> {
                  TicketEvidenceJpaEntity e = new TicketEvidenceJpaEntity();
                  e.setTicket(entity);
                  e.setFile(f);
                  return e;
                })
            .collect(Collectors.toList());
    entity.setEvidences(evidenceEntities);

    return entity;
  }

  public void updateEntityFromDomain(Ticket domain, TicketJpaEntity entity, UserJpaEntity handler) {
    entity.setStatus(domain.getStatus());
    entity.setResolutionNote(domain.getResolutionNote());
    entity.setHandler(handler);
  }
}
