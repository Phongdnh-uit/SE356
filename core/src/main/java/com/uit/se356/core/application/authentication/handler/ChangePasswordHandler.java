package com.uit.se356.core.application.authentication.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.application.authentication.command.ChangePasswordCommand;
import com.uit.se356.core.application.authentication.port.out.PasswordEncoder;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.exception.UserErrorCode;
import com.uit.se356.core.domain.vo.authentication.UserId;
import java.util.Optional;

public class ChangePasswordHandler implements CommandHandler<ChangePasswordCommand, Void> {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SecurityUtil<UserId> securityUtil;

  public ChangePasswordHandler(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      SecurityUtil<UserId> securityUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.securityUtil = securityUtil;
  }

  @Override
  public Void handle(ChangePasswordCommand command) {
    UserId userId = securityUtil.getCurrentUserPrincipal().get().getId();
    // Tìm kiếm người dùng dựa trên userId
    Optional<User> userOptional = userRepository.findById(userId);

    if (userOptional.isEmpty()) {
      throw new AppException(UserErrorCode.USER_NOT_FOUND);
    }

    User user = userOptional.get();

    // Kiểm tra mật khẩu cũ có hợp lệ không
    if (!passwordEncoder.matches(command.oldPassword(), user.getPasswordHash())) {
      throw new AppException(AuthErrorCode.INVALID_CREDENTIALS);
    }

    // Mã hóa và cập nhật mật khẩu mới
    String newPasswordHash = passwordEncoder.encode(command.newPassword());
    user.changePassword(newPasswordHash);

    userRepository.update(user);

    return null;
  }
}
