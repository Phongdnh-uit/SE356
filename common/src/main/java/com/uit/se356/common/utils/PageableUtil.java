package com.uit.se356.common.utils;

import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.dto.SearchRequest;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {
  public static Pageable createPageable(SearchPageable searchPageable) {
    List<Sort.Order> sortOrders =
        searchPageable.sorts().stream()
            .map(
                sort -> {
                  return parseSort(sort);
                })
            .toList();
    // 2. Tạo pageable với sort

    return PageRequest.of(searchPageable.page(), searchPageable.size(), Sort.by(sortOrders));
  }

  public static Pageable createPageable(SearchRequest searchRequest) {
    List<Sort.Order> sortOrders =
        searchRequest.sorts().stream()
            .map(
                sort -> {
                  return parseSort(sort);
                })
            .toList();
    return Pageable.unpaged(Sort.by(sortOrders));
  }

  private static Sort.Order parseSort(String sort) {
    String[] parts = sort.split(":");
    if (parts.length != 2) {
      throw new AppException(CommonErrorCode.INVALID_SORT_ORDER);
    }
    String property = parts[0];
    Sort.Direction direction =
        parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    return new Sort.Order(direction, property);
  }
}
