package com.uit.se356.core.application.seeding;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.authentication.port.out.PasswordEncoder;
import com.uit.se356.core.application.authentication.port.out.RoleRepository;
import com.uit.se356.core.application.seeding.port.BootstrapConfigPort;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.application.wallet.command.CreateWalletCommand;
import com.uit.se356.core.domain.constants.RoleName;
import com.uit.se356.core.domain.entities.authentication.Role;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.vo.authentication.Email;
import com.uit.se356.core.domain.vo.authentication.PhoneNumber;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.UserStatus;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeedingService {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final IdGenerator idGenerator;
  private final BootstrapConfigPort bootstrapConfigPort;
  private final PasswordEncoder passwordEncoder;
  private final CommandBus commandBus;

  @Transactional
  public void seedData() {
    seedDefaultRoles();
    // Sau khi đã đảm bảo các role đã tồn tại, seed tài khoản admin nếu cần thiết
    UserId userId = seedDefaultAdmin();
    // Sau khi đã có tài khoản admin, tạo ví cho admin nếu cần thiết
    if (userId != null) {
      seedAdminWallet(userId);
    }
  }

  private void seedDefaultRoles() {
    // Seed vai trò nếu chưa tồn tại, sau đó gắn mặc định cho người dùng USER nếu chưa có
    boolean isDefaultRoleExists = roleRepository.findDefault().isPresent();
    for (RoleName roleName : RoleName.values()) {
      if (!roleRepository.existsByName(roleName.name())) {
        Role role =
            Role.createSystemRole(
                new RoleId(idGenerator.generate().toString()), roleName.name(), "");
        if (!isDefaultRoleExists && roleName == RoleName.USER) {
          role.markAsDefault();
        }
        roleRepository.create(role);
      }
    }
  }

  private UserId seedDefaultAdmin() {
    // Seed tài khoản admin nếu chưa tồn tại
    // Dựa vào thông tin bootstrap để tạo tài khoản, các lần khởi tạo sau sẽ bỏ qua nếu đã tồn tại
    // một tài khoản có role admin

    // 1. Kiểm tra liệu role đã được seed chưa
    Role defaultAdminRole =
        roleRepository
            .findByName(RoleName.ADMIN.name())
            .orElseThrow(() -> new AppException(AuthErrorCode.ROLE_NOT_FOUND));

    List<User> adminUsers = userRepository.findByRoleId(defaultAdminRole.getId());

    if (adminUsers.isEmpty()) {
      // 2. Nếu chưa có admin nào, tạo tài khoản admin mới dựa trên thông tin bootstrap
      String fullName = bootstrapConfigPort.getAdminFullName();
      String email = bootstrapConfigPort.getAdminEmail();
      String password = bootstrapConfigPort.getAdminPassword();
      String phoneNumber = bootstrapConfigPort.getAdminPhoneNumber();

      UserId adminUserId = new UserId(idGenerator.generate().toString());

      String passwordHash = passwordEncoder.encode(password);
      User adminUser =
          User.create(
              adminUserId,
              fullName,
              new Email(email),
              passwordHash,
              new PhoneNumber(phoneNumber),
              defaultAdminRole.getId());
      adminUser.verifyEmail();
      adminUser.verifyPhone();
      adminUser.updateStatus(UserStatus.ACTIVE);

      userRepository.create(adminUser);
      return adminUserId;
    }

    return null; // Nếu đã tồn tại admin, trả về null để không tạo ví mới
  }

  private void seedAdminWallet(UserId adminUserId) {
    // Tạo ví cho admin sau khi đã tạo tài khoản
    CreateWalletCommand createWalletCommand = new CreateWalletCommand(adminUserId);
    commandBus.dispatch(createWalletCommand);
  }
}
