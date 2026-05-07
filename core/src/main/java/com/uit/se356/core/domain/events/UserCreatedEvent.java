package com.uit.se356.core.domain.events;

import com.uit.se356.core.domain.vo.authentication.UserId;

/** Event fired when a new user is created. */
public record UserCreatedEvent(UserId userId) {}
