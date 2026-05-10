package com.uit.se356.core.domain.entities.authentication;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.domain.exception.UserErrorCode;
import com.uit.se356.core.domain.vo.authentication.Email;
import com.uit.se356.core.domain.vo.authentication.PhoneNumber;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.UserStatus;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class User {
  private static final Map<UserStatus, Set<UserStatus>> allowedTransitions =
      Map.of(
          UserStatus.UNVERIFIED, Set.of(UserStatus.ACTIVE),
          UserStatus.ACTIVE, Set.of(UserStatus.DELETED, UserStatus.BLOCKED),
          UserStatus.BLOCKED, Set.of(UserStatus.ACTIVE));

  private final UserId id;
  private String fullName;
  private PhoneNumber phoneNumber;
  private Email email;
  private String passwordHash;
  private boolean phoneVerified;
  private boolean emailVerified;
  private UserStatus status;
  private RoleId roleId;

  // Hàm khởi tạo đầy đủ
  // Note: private nhằm chắc chắn User luôn ở trạng thái đúng
  private User(
      UserId userId,
      String fullName,
      Email email,
      String passwordHash,
      PhoneNumber phoneNumber,
      UserStatus status,
      boolean phoneVerified,
      boolean emailVerified,
      RoleId roleId) {
    this.id = userId;
    this.fullName = fullName;
    this.email = email;
    this.passwordHash = passwordHash;
    this.phoneNumber = phoneNumber;
    this.phoneVerified = phoneVerified;
    this.emailVerified = emailVerified;
    this.status = status;
    this.roleId = roleId;
  }

  // ============================ FACTORY ============================
  public static User create(
      UserId userId,
      String fullName,
      Email email,
      String passwordHash,
      PhoneNumber phoneNumber,
      RoleId roleId) {
    // Hàm kiểm tra null cho các tham số bắt buộc
    Objects.requireNonNull(userId);
    Objects.requireNonNull(fullName);
    Objects.requireNonNull(email);
    Objects.requireNonNull(passwordHash);
    Objects.requireNonNull(phoneNumber);
    Objects.requireNonNull(roleId);

    // Trạng thái ban đầu của người dùng là UNVERIFIED, chưa xác thực email và sđt
    return new User(
        userId,
        fullName,
        email,
        passwordHash,
        phoneNumber,
        UserStatus.UNVERIFIED,
        false,
        false,
        roleId);
  }

  public static User rehydrate(
      UserId userId,
      String fullName,
      Email email,
      String passwordHash,
      PhoneNumber phoneNumber,
      UserStatus status,
      boolean phoneVerified,
      boolean emailVerified,
      RoleId roleId) {
    return new User(
        userId,
        fullName,
        email,
        passwordHash,
        phoneNumber,
        status,
        phoneVerified,
        emailVerified,
        roleId);
  }

  public static User createOAuthUser(
      UserId userId, String fullName, Email email, PhoneNumber phoneNumber, RoleId roleId) {
    // Hàm kiểm tra null cho các tham số bắt buộc
    Objects.requireNonNull(userId);
    Objects.requireNonNull(fullName);
    Objects.requireNonNull(email);
    Objects.requireNonNull(phoneNumber);
    return new User(
        userId,
        fullName,
        email,
        null, // OAuth user không có password hash
        phoneNumber,
        UserStatus.ACTIVE, // OAuth user mặc định là ACTIVE vì đã được xác thực qua provider
        true, // OAuth user mặc định đã xác thực email
        true, // OAuth user mặc định đã xác thực sđt
        roleId);
  }

  // ============================ BEHAVIOR ============================
  public void changePassword(String newPasswordHash) {
    Objects.requireNonNull(newPasswordHash);
    this.passwordHash = newPasswordHash;
  }

  public void updateStatus(UserStatus next) {
    // Sơ đồ trạng thái người dùng
    // UNVERIFIED -> ACTIVE
    // ACTIVE -> DELETED, BLOCKED
    // BLOCKED -> ACTIVE

    // Riêng trạng thái UNVERIED cần kiểm tra thêm điều kiện xác thực email/sđt trước khi chuyển
    // sang ACTIVE
    UserStatus currentStatus = this.status;
    if (currentStatus == UserStatus.UNVERIFIED && next == UserStatus.ACTIVE) {
      if (!this.emailVerified || !this.phoneVerified) {
        throw new AppException(UserErrorCode.CANNOT_ACTIVATE_UNVERIFIED_USER);
      }
    }

    if (!allowedTransitions.containsKey(currentStatus)
        || !allowedTransitions.get(currentStatus).contains(next)) {
      throw new AppException(UserErrorCode.INVALID_USER_STATUS_TRANSITION);
    }

    this.status = next;
  }

  public void verifyEmail() {
    this.emailVerified = true;
  }

  public void verifyPhone() {
    this.phoneVerified = true;
  }

  public void updateProfile(String newFullName) {
    if (newFullName != null && !newFullName.isBlank()) {
      this.fullName = newFullName;
    }
  }

  public void updateRole(RoleId newRoleId) {
    Objects.requireNonNull(newRoleId);
    this.roleId = newRoleId;
  }

  // ============================ GETTERS ============================
  public UserId getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public PhoneNumber getPhoneNumber() {
    return phoneNumber;
  }

  public Email getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public boolean isPhoneVerified() {
    return phoneVerified;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  public UserStatus getStatus() {
    return status;
  }

  public RoleId getRoleId() {
    return roleId;
  }
}
