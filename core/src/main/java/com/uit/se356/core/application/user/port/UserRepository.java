package com.uit.se356.core.application.user.port;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.user.projections.UserSummaryProjection;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.vo.authentication.Email;
import com.uit.se356.core.domain.vo.authentication.PhoneNumber;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.UserStatus;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
  User create(User newUser);

  User update(User userToUpdate);

  Optional<User> findById(UserId id);

  Optional<User> findByEmail(Email email);

  Optional<User> findByPhoneNumber(PhoneNumber phoneNumber);

  // Cần tối ưu, nếu load hết sẽ gây tràn bộ nhớ
  List<User> findByStatus(UserStatus status);

  // Vẫn đang dùng framework, cần sửa lại
  /** Phiên bản phân trang của findByStatus. */
  PageResponse<User> findByStatus(UserStatus status, SearchPageable pageable);

  // Cần tối ưu, nếu load hết sẽ gây tràn bộ nhớ
  //  List<User> findAll();

  /** Phiên bản phân trang của findAll. */
  PageResponse<User> findAll(SearchPageable pageable);

  /** Phiên bản phân trang projection của findByStatus. */
  PageResponse<UserSummaryProjection> findByStatusProjection(
      UserStatus status, SearchPageable pageable);

  /** Phiên bản phân trang projection của findAll. */
  PageResponse<UserSummaryProjection> findAllProjection(SearchPageable pageable);

  // Cần tối ưu, nếu load hết sẽ gây tràn bộ nhớ
  List<User> findByRoleId(RoleId roleId);

  void delete(UserId id);

  boolean existsByEmail(Email email);

  boolean existsByPhoneNumber(PhoneNumber phoneNumber);

  boolean existsById(UserId id);
}
