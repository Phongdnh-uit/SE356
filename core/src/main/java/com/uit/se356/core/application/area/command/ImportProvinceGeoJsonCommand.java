package com.uit.se356.core.application.area.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.area.result.ImportResult;
import org.springframework.web.multipart.MultipartFile;

public record ImportProvinceGeoJsonCommand(MultipartFile file, int batchSize)
    implements Command<ImportResult> {

  private static final int DEFAULT_BATCH_SIZE = 100;

  public ImportProvinceGeoJsonCommand(MultipartFile file) {
    this(file, DEFAULT_BATCH_SIZE);
  }

  public ImportProvinceGeoJsonCommand {
    if (file == null || file.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, "GeoJSON file is required");
    }
    if (batchSize <= 0) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, "Batch size must be greater than 0");
    }
  }
}
