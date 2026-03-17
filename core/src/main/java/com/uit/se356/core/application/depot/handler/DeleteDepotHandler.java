package com.uit.se356.core.application.depot.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.depot.command.DeleteDepotCommand;
import com.uit.se356.core.application.depot.port.DepotRepository;
import com.uit.se356.core.application.depot.port.RouteCheckingPort;
import com.uit.se356.core.domain.entities.depot.Depot;
import com.uit.se356.core.domain.exception.DepotErrorCode;

public class DeleteDepotHandler implements CommandHandler<DeleteDepotCommand, Void> {
  private final DepotRepository depotRepository;
  private final RouteCheckingPort routeCheckingPort; // Inject Port để check Rule

  public DeleteDepotHandler(DepotRepository depotRepository, RouteCheckingPort routeCheckingPort) {
    this.depotRepository = depotRepository;
    this.routeCheckingPort = routeCheckingPort;
  }

  @Override
  @HasPermission("depot:delete")
  public Void handle(DeleteDepotCommand command) {
    Depot depot =
        depotRepository
            .findById(command.id())
            .orElseThrow(() -> new AppException(DepotErrorCode.DEPOT_NOT_FOUND));

    if (routeCheckingPort.hasActiveRoutesFromDepot(depot.getId())) {
      throw new AppException(DepotErrorCode.DEPOT_IN_USE);
    }

    depotRepository.delete(depot.getId());
    return null;
  }
}
