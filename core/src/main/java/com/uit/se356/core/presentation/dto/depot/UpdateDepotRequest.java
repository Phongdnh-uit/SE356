package com.uit.se356.core.presentation.dto.depot;

import com.uit.se356.core.domain.vo.depot.DepotType;

public record UpdateDepotRequest(String name, DepotType type, double latitude, double longitude) {}
