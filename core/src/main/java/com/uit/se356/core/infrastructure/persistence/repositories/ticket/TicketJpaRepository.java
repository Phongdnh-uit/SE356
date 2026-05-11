package com.uit.se356.core.infrastructure.persistence.repositories.ticket;

import com.uit.se356.core.infrastructure.persistence.entities.ticket.TicketJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketJpaRepository
    extends JpaRepository<TicketJpaEntity, String>, JpaSpecificationExecutor<TicketJpaEntity> {

  @Query("SELECT t FROM TicketJpaEntity t WHERE t.reporter.id = :reporterId")
  Page<TicketJpaEntity> findByReporterId(@Param("reporterId") String reporterId, Pageable pageable);
}
