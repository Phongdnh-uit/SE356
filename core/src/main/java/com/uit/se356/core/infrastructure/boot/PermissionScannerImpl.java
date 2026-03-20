package com.uit.se356.core.infrastructure.boot;

import com.uit.se356.common.dto.PermissionDefinition;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.security.PermissionScanner;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionScannerImpl implements PermissionScanner {
  private final ApplicationContext applicationContext;

  @Override
  public List<PermissionDefinition> scan(String packageName) {

    // 1. Dùng set để tránh trùng lặp nếu có nhiều method cùng permission
    Set<PermissionDefinition> permissions = new LinkedHashSet<>();

    // 2. Lấy tất cả tên bean mà Spring đã đăng ký
    for (String beanName : applicationContext.getBeanDefinitionNames()) {
      try {
        // 3. Lấy class gốc, tránh các class proxy do Spring tạo ra (cho @Transactional, @Async,...)
        Class<?> beanType = applicationContext.getType(beanName);
        // Tìm class gốc
        Class<?> targetClass = ClassUtils.getUserClass(beanType);

        // Nếu không lấy được class (ví dụ bean là một FactoryBean), bỏ qua
        if (targetClass == null) continue;

        // 4. Chỉ quét các bean thuộc package, bỏ qua các bean của framework
        if (targetClass.getPackage() == null || !targetClass.getName().startsWith(packageName))
          continue;

        // 5. Sử dụng MethodIntrospector để quét cả các method kế thừa hoặc từ Interface
        Map<Method, HasPermission> annotatedMethods =
            MethodIntrospector.selectMethods(
                targetClass,
                (MethodIntrospector.MetadataLookup<HasPermission>)
                    method ->
                        AnnotatedElementUtils.findMergedAnnotation(method, HasPermission.class));

        annotatedMethods.forEach(
            (method, annotation) -> {
              permissions.add(
                  new PermissionDefinition(
                      annotation.name(),
                      annotation.description(),
                      annotation.resource(),
                      annotation.action(),
                      annotation.condition()));
            });

      } catch (Exception e) {
        // Log lỗi nhưng không dừng quá trình quét
        log.warn("Failed to scan bean '{}': {}", beanName, e.getMessage());
      }
    }

    return permissions.stream().toList();
  }
}
