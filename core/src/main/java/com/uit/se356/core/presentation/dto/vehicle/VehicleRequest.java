package com.uit.se356.core.presentation.dto.vehicle;

import com.uit.se356.core.domain.vo.vehicle.VehicleType;

public record VehicleRequest(
    String licensePlate, VehicleType type, Double maxWeight, Double maxVolume, String shipperId) {}
