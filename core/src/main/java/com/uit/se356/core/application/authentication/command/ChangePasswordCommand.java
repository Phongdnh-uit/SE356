package com.uit.se356.core.application.authentication.command;

import com.uit.se356.common.dto.Command;

public record ChangePasswordCommand(String oldPassword, String newPassword)
    implements Command<Void> {}
