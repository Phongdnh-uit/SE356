package com.uit.se356.core.application.vehicle.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.vehicle.command.SaveVehicleCommand;
import com.uit.se356.core.application.vehicle.port.VehicleRepository;
import com.uit.se356.core.application.vehicle.result.VehicleResult;
import com.uit.se356.core.domain.constants.PermissionConstant.Action;
import com.uit.se356.core.domain.constants.PermissionConstant.Resource;
import com.uit.se356.core.domain.entities.vehicle.Vehicle;
import com.uit.se356.core.domain.exception.VehicleErrorCode;
import com.uit.se356.core.domain.vo.vehicle.PhysicalCapacity;
import com.uit.se356.core.domain.vo.vehicle.VehicleId;

public class SaveVehicleHandler implements CommandHandler<SaveVehicleCommand, VehicleResult> {

  private final VehicleRepository vehicleRepository;
  private final IdGenerator idGenerator;

  public SaveVehicleHandler(VehicleRepository vehicleRepository, IdGenerator idGenerator) {
    this.vehicleRepository = vehicleRepository;
    this.idGenerator = idGenerator;
  }

  @Override
  @HasPermission(
      name = "vehicle:save",
      description = "Quản lý phương tiện: Thêm mới hoặc cập nhật thông tin phương tiện",
      resource = Resource.VEHICLE,
      action = Action.CREATE)
  public VehicleResult handle(SaveVehicleCommand command) {
    boolean isUpdate = command.id() != null && !command.id().value().isBlank();

    // 1. Validation BR(2): Check Unique License Plate
    if (isUpdate) {
      if (vehicleRepository.existsByLicensePlateAndIdNot(command.licensePlate(), command.id())) {
        throw new AppException(VehicleErrorCode.DUPLICATE_LICENSE_PLATE);
      }
    } else {
      if (vehicleRepository.existsByLicensePlate(command.licensePlate())) {
        throw new AppException(VehicleErrorCode.DUPLICATE_LICENSE_PLATE);
      }
    }

    // 2. Validation BR(3): Check Shipper Assignment Availability
    if (command.shipperId() != null && !command.shipperId().value().isBlank()) {
      boolean isShipperBusy =
          isUpdate
              ? vehicleRepository.existsByShipperIdAndIdNot(command.shipperId(), command.id())
              : vehicleRepository.existsByShipperId(command.shipperId());

      if (isShipperBusy) {
        throw new AppException(
            VehicleErrorCode.SHIPPER_ALREADY_ASSIGNED,
            "This Shipper is already assigned to another vehicle.");
      }
    }

    // 3. Khởi tạo Capacity
    PhysicalCapacity capacity = new PhysicalCapacity(command.maxWeight(), command.maxVolume());

    // 4. Thực hiện Update hoặc Insert
    Vehicle vehicle;
    if (isUpdate) {
      vehicle =
          vehicleRepository
              .findById(command.id())
              .orElseThrow(() -> new AppException(VehicleErrorCode.VEHICLE_NOT_FOUND));

      vehicle.update(command.licensePlate(), command.type(), capacity, command.shipperId());
    } else {
      String newId = idGenerator.generate().toString();
      vehicle =
          Vehicle.create(
              new VehicleId(newId),
              command.licensePlate(),
              command.type(),
              capacity,
              command.shipperId());
    }

    Vehicle savedVehicle = vehicleRepository.save(vehicle);
    return VehicleResult.fromEntity(savedVehicle);
  }
}
