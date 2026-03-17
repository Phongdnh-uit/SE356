package com.uit.se356.core.application.depot.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.core.domain.vo.depot.DepotId;

public record DeleteDepotCommand(DepotId id) implements Command<Void> {}
