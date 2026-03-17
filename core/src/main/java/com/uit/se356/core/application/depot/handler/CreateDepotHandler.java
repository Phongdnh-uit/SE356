package com.uit.se356.core.application.depot.handler;

import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.depot.command.CreateDepotCommand;
import com.uit.se356.core.application.depot.port.DepotRepository;
import com.uit.se356.core.application.depot.result.DepotResult;
import com.uit.se356.core.domain.entities.depot.Depot;
import com.uit.se356.core.domain.exception.DepotErrorCode;
import com.uit.se356.core.domain.vo.area.Coordinate;
import com.uit.se356.core.domain.vo.depot.DepotId;
import java.util.ArrayList;
import java.util.List;

public class CreateDepotHandler implements CommandHandler<CreateDepotCommand, DepotResult> {
  private final DepotRepository depotRepository;
  private final IdGenerator idGenerator;

  // Định nghĩa khoảng cách tối thiểu (7 km)
  private static final double MIN_DISTANCE_KM = 7.0;

  public CreateDepotHandler(DepotRepository depotRepository, IdGenerator idGenerator) {
    this.depotRepository = depotRepository;
    this.idGenerator = idGenerator;
  }

  @Override
  @HasPermission("depot:create")
  public DepotResult handle(CreateDepotCommand command) {
    List<FieldError> errors = new ArrayList<>();
    // BR: Kiểm tra khoảng cách tối thiểu với các kho hiện tại
    if (depotRepository.hasNearbyDepot(command.lat(), command.lng(), MIN_DISTANCE_KM, null)) {
      errors.add(
          new FieldError("lat,lng", "error.depot.too_close", new Object[] {MIN_DISTANCE_KM}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(DepotErrorCode.DEPOT_TOO_CLOSE, MIN_DISTANCE_KM);
    }

    String newId = idGenerator.generate().toString();
    Coordinate coordinate = new Coordinate(command.lat(), command.lng());

    Depot depot = Depot.create(new DepotId(newId), command.name(), command.type(), coordinate);
    Depot savedDepot = depotRepository.create(depot);

    return DepotResult.fromEntity(savedDepot);
  }
}
