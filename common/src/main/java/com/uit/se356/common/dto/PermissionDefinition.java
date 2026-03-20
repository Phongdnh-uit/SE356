package com.uit.se356.common.dto;

public record PermissionDefinition(
    String name, String description, String resource, String action, String condition) {}
