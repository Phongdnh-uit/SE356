package com.uit.se356.core.application.depot.query;

import com.uit.se356.common.dto.Query;
import com.uit.se356.core.application.depot.result.DepotResult;
import com.uit.se356.core.domain.vo.depot.DepotId;

public record GetDepotByIdQuery(DepotId id) implements Query<DepotResult> {}
