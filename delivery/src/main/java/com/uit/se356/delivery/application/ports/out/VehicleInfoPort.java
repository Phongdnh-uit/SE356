package com.uit.se356.delivery.application.ports.out;

import com.uit.se356.delivery.domain.entities.DeliveryVehicle;
import com.uit.se356.delivery.domain.vo.VehicleId;
import java.util.List;

public interface VehicleInfoPort {
  List<DeliveryVehicle> getAvailableVehicles(List<VehicleId> vehicleIds);
}
