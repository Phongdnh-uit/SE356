package com.uit.se356.core.presentation.dto.user;

import com.uit.se356.core.domain.vo.authentication.UserStatus;

public record UpdateUserStatusRequest(UserStatus status) {}
