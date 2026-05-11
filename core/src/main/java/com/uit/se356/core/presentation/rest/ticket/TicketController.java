package com.uit.se356.core.presentation.rest.ticket;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.core.application.ticket.command.AddTicketCommentCommand;
import com.uit.se356.core.application.ticket.command.CreateTicketCommand;
import com.uit.se356.core.application.ticket.command.ProcessTicketCommand;
import com.uit.se356.core.application.ticket.query.GetAllTicketsQuery;
import com.uit.se356.core.application.ticket.query.GetMyTicketsQuery;
import com.uit.se356.core.application.ticket.query.GetTicketDetailsQuery;
import com.uit.se356.core.application.ticket.result.TicketResult;
import com.uit.se356.core.presentation.dto.ticket.AddCommentRequest;
import com.uit.se356.core.presentation.dto.ticket.CreateTicketRequest;
import com.uit.se356.core.presentation.dto.ticket.ProcessTicketRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Ticket")
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@RestController
public class TicketController {
  private final CommandBus commandBus;
  private final QueryBus queryBus;

  @PostMapping
  public ResponseEntity<ApiResponse<TicketResult>> createTicket(
      @RequestBody CreateTicketRequest request) {
    CreateTicketCommand command =
        new CreateTicketCommand(
            request.getSummary(), request.getDescription(), request.getEvidenceFileIds());
    return ResponseEntity.ok(
        ApiResponse.ok(commandBus.dispatch(command), "Ticket created successfully"));
  }

  @GetMapping("/my")
  public ResponseEntity<ApiResponse<PageResponse<TicketResult>>> getMyTickets(
      @ParameterObject @ModelAttribute SearchPageable query) {
    return ResponseEntity.ok(
        ApiResponse.ok(
            queryBus.dispatch(new GetMyTicketsQuery(query)), "My tickets retrieved successfully"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<TicketResult>>> getAllTickets(
      @ParameterObject @ModelAttribute SearchPageable query) {
    return ResponseEntity.ok(
        ApiResponse.ok(
            queryBus.dispatch(new GetAllTicketsQuery(query)),
            "All tickets retrieved successfully"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TicketResult>> getTicketDetails(@PathVariable("id") String id) {
    return ResponseEntity.ok(
        ApiResponse.ok(
            queryBus.dispatch(new GetTicketDetailsQuery(id)),
            "Ticket details retrieved successfully"));
  }

  @PostMapping("/{id}/comments")
  public ResponseEntity<ApiResponse<TicketResult>> addComment(
      @PathVariable("id") String id, @RequestBody AddCommentRequest request) {
    AddTicketCommentCommand command =
        new AddTicketCommentCommand(id, request.getContent(), request.getEvidenceFileIds());
    return ResponseEntity.ok(
        ApiResponse.ok(commandBus.dispatch(command), "Comment added successfully"));
  }

  @PatchMapping("/{id}/process")
  public ResponseEntity<ApiResponse<TicketResult>> processTicket(
      @PathVariable("id") String id, @RequestBody ProcessTicketRequest request) {
    ProcessTicketCommand command =
        new ProcessTicketCommand(id, request.getAction(), request.getResolutionNote());
    return ResponseEntity.ok(
        ApiResponse.ok(commandBus.dispatch(command), "Ticket processed successfully"));
  }
}
