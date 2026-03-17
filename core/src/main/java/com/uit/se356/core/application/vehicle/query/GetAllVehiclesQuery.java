package com.uit.se356.core.application.vehicle.query;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.Query;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.vehicle.projecttion.VehicleSummaryProjection;

public record GetAllVehiclesQuery(SearchPageable pageable)
    implements Query<PageResponse<VehicleSummaryProjection>> {}
