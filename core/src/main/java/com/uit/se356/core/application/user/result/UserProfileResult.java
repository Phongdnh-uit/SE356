package com.uit.se356.core.application.user.result;

public record UserProfileResult(
    String id,
    String fullName,
    String email,
    String phoneNumber,
    String status,
    String roleName,
    boolean phoneVerified,
    boolean emailVerified) {}
