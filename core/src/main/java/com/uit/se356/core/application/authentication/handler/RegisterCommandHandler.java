package com.uit.se356.core.application.authentication.handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.authentication.command.RegisterCommand;
import com.uit.se356.core.application.authentication.port.out.AuthCacheRepository;
import com.uit.se356.core.application.authentication.port.out.PasswordEncoder;
import com.uit.se356.core.application.authentication.port.out.RoleRepository;
import com.uit.se356.core.application.authentication.query.SendVerificationCodeQuery;
import com.uit.se356.core.application.authentication.result.RegisterResult;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.domain.entities.authentication.Role;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.vo.authentication.CodePurpose;
import com.uit.se356.core.domain.vo.authentication.Email;
import com.uit.se356.core.domain.vo.authentication.PhoneNumber;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.VerificationChannel;
import java.util.ArrayList;
import java.util.List;

public class RegisterCommandHandler implements CommandHandler<RegisterCommand, RegisterResult> {
  private final AuthCacheRepository cacheRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final IdGenerator idGenerator;
  private final QueryBus queryBus;
  private final RoleRepository roleRepository;

  public RegisterCommandHandler(
      AuthCacheRepository cacheRepository,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      IdGenerator idGenerator,
      QueryBus queryBus,
      RoleRepository roleRepository) {
    this.cacheRepository = cacheRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.idGenerator = idGenerator;
    this.roleRepository = roleRepository;
    this.queryBus = queryBus;
  }

  @Override
  public RegisterResult handle(RegisterCommand command) {
    List<FieldError> errors = new ArrayList<>();

    PhoneNumber phoneNumber = new PhoneNumber(getPhoneNumber(command.verificationToken()));
    Email email = new Email(command.email());
    if (userRepository.existsByEmail(email)) {
      errors.add(
          new FieldError(
              "email", CommonErrorCode.ALREADY_EXISTS.getMessageKey(), new Object[] {"email"}));
    }
    if (userRepository.existsByPhoneNumber(phoneNumber)) {
      errors.add(
          new FieldError(
              "phoneNumber",
              CommonErrorCode.ALREADY_EXISTS.getMessageKey(),
              new Object[] {"phoneNumber"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
    Role defaultRole =
        roleRepository
            .findDefault()
            .orElseThrow(() -> new AppException(AuthErrorCode.ROLE_NOT_FOUND));
    UserId userId = new UserId(idGenerator.generate().toString());
    User user =
        User.create(
            userId,
            command.fullName(),
            email,
            passwordEncoder.encode(command.password()),
            phoneNumber,
            defaultRole.getId());
    user.verifyPhone();
    user = userRepository.create(user);
    // Gửi email xác nhận
    SendVerificationCodeQuery sendEmailVerificationCodeQuery =
        new SendVerificationCodeQuery(
            CodePurpose.EMAIL_VERIFICATION, VerificationChannel.EMAIL, email.value());
    queryBus.dispatch(sendEmailVerificationCodeQuery);

    // Trả về kết quả đăng ký
    return new RegisterResult(
        user.getId().value(),
        user.getFullName(),
        user.getEmail().value(),
        user.getPhoneNumber().value(),
        user.isEmailVerified(),
        user.isPhoneVerified());
  }

  private String getPhoneNumber(String verificationToken) {
    try {
      FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(verificationToken);
      return decodedToken.getClaims().get("phone_number").toString();
    } catch (FirebaseAuthException e) {
      throw new AppException(AuthErrorCode.INVALID_VERIFICATION_CODE);
    }
  }
}
