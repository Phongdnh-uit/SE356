package com.uit.se356.core.infrastructure.repositories.ticket;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.ticket.port.out.TicketRepository;
import com.uit.se356.core.domain.entities.ticket.Ticket;
import com.uit.se356.core.domain.entities.ticket.TicketComment;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.ticket.TicketId;
import com.uit.se356.core.domain.vo.upload.FileId;
import com.uit.se356.core.infrastructure.persistence.entities.authentication.UserJpaEntity;
import com.uit.se356.core.infrastructure.persistence.entities.ticket.TicketCommentJpaEntity;
import com.uit.se356.core.infrastructure.persistence.entities.ticket.TicketEvidenceJpaEntity;
import com.uit.se356.core.infrastructure.persistence.entities.ticket.TicketJpaEntity;
import com.uit.se356.core.infrastructure.persistence.entities.upload.FileJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.ticket.TicketPersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.authentication.UserJpaRepository;
import com.uit.se356.core.infrastructure.persistence.repositories.ticket.TicketJpaRepository;
import com.uit.se356.core.infrastructure.persistence.repositories.upload.FileJpaRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {

  private final TicketJpaRepository ticketJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final FileJpaRepository fileJpaRepository;
  private final TicketPersistenceMapper ticketMapper;

  @Override
  @Transactional
  public Ticket save(Ticket ticket) {
    Optional<TicketJpaEntity> existing = ticketJpaRepository.findById(ticket.getId().value());

    TicketJpaEntity entity;
    if (existing.isPresent()) {
      entity = existing.get();
      UserJpaEntity handler =
          ticket.getHandlerId() != null
              ? userJpaRepository.getReferenceById(ticket.getHandlerId().value())
              : null;
      ticketMapper.updateEntityFromDomain(ticket, entity, handler);
    } else {
      UserJpaEntity reporter = userJpaRepository.getReferenceById(ticket.getReporterId().value());
      UserJpaEntity handler =
          ticket.getHandlerId() != null
              ? userJpaRepository.getReferenceById(ticket.getHandlerId().value())
              : null;
      List<FileJpaEntity> evidenceFiles =
          fileJpaRepository.findAllById(
              ticket.getEvidenceFileIds().stream().map(FileId::value).toList());
      entity = ticketMapper.toEntity(ticket, reporter, handler, evidenceFiles);
    }

    // Handle Comments (Add new ones)
    Set<String> existingCommentIds =
        entity.getComments().stream()
            .map(TicketCommentJpaEntity::getId)
            .collect(Collectors.toSet());

    for (TicketComment domainComment : ticket.getComments()) {
      if (!existingCommentIds.contains(domainComment.getId())) {
        TicketCommentJpaEntity commentEntity = new TicketCommentJpaEntity();
        commentEntity.setId(domainComment.getId());
        commentEntity.setTicket(entity);
        commentEntity.setAuthor(
            userJpaRepository.getReferenceById(domainComment.getAuthorId().value()));
        commentEntity.setContent(domainComment.getContent());
        commentEntity.setCreatedAt(domainComment.getCreatedAt());

        List<FileJpaEntity> commentFiles =
            fileJpaRepository.findAllById(
                domainComment.getEvidenceFileIds().stream().map(FileId::value).toList());

        List<TicketEvidenceJpaEntity> commentEvidenceEntities =
            commentFiles.stream()
                .map(
                    f -> {
                      TicketEvidenceJpaEntity e = new TicketEvidenceJpaEntity();
                      e.setComment(commentEntity);
                      e.setFile(f);
                      return e;
                    })
                .toList();
        commentEntity.setEvidences(commentEvidenceEntities);

        entity.getComments().add(commentEntity);
      }
    }

    TicketJpaEntity saved = ticketJpaRepository.save(entity);
    return ticketMapper.toDomain(saved);
  }

  @Override
  public Optional<Ticket> findById(TicketId id) {
    return ticketJpaRepository.findById(id.value()).map(ticketMapper::toDomain);
  }

  @Override
  public PageResponse<Ticket> findAll(SearchPageable searchCriteria) {
    Specification<TicketJpaEntity> spec = RSQLJPASupport.toSpecification(searchCriteria.filter());
    Pageable pageable =
        PageRequest.of(
            searchCriteria.page(),
            searchCriteria.size(),
            Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<TicketJpaEntity> page = ticketJpaRepository.findAll(spec, pageable);
    return PageResponse.of(page.map(ticketMapper::toDomain));
  }

  @Override
  public PageResponse<Ticket> findByReporterId(UserId reporterId, SearchPageable searchCriteria) {
    Pageable pageable =
        PageRequest.of(
            searchCriteria.page(),
            searchCriteria.size(),
            Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<TicketJpaEntity> page = ticketJpaRepository.findByReporterId(reporterId.value(), pageable);
    return PageResponse.of(page.map(ticketMapper::toDomain));
  }
}
