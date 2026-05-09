package com.uit.se356.core.application.user.projections;

import com.uit.se356.core.domain.vo.authentication.Email;
import com.uit.se356.core.domain.vo.authentication.PhoneNumber;
import com.uit.se356.core.domain.vo.authentication.UserStatus;

public interface UserSummaryProjection {
  Long getId();

  String getFullname();

  PhoneNumber getPhoneNumber();

  Email getEmail();

  UserStatus getUserStatus();
}
