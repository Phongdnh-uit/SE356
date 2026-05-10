package com.uit.se356.core.application.user.projections;

import com.uit.se356.core.domain.vo.authentication.UserStatus;

public record UserSummaryProjection(
    String id,
    String fullname,
    String roleName,
    String phoneNumber,
    String email,
    UserStatus userStatus) {}
