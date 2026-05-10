package com.uit.se356.core.presentation.rest.area;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.core.application.area.command.ImportProvinceGeoJsonCommand;
import com.uit.se356.core.application.area.command.ImportWardGeoJsonCommand;
import com.uit.se356.core.application.area.result.ImportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Area Management (Admin)")
@RestController
@RequestMapping("/api/v1/admin/areas")
@RequiredArgsConstructor
public class AreaImportController {

  private final CommandBus commandBus;

  @Operation(summary = "Import Provinces from GeoJSON file")
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(value = "/provinces/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ImportResult>> importProvinces(
      @RequestPart("file") MultipartFile file) {

    ImportProvinceGeoJsonCommand command = new ImportProvinceGeoJsonCommand(file);
    ImportResult importedCount = commandBus.dispatch(command);

    return ResponseEntity.ok(
        ApiResponse.ok(importedCount, "Successfully imported " + importedCount + " provinces."));
  }

  @Operation(summary = "Import Wards from GeoJSON file")
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(value = "/wards/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ImportResult>> importWards(
      @RequestPart("file") MultipartFile file) {

    ImportWardGeoJsonCommand command = new ImportWardGeoJsonCommand(file);
    ImportResult importedCount = commandBus.dispatch(command);

    return ResponseEntity.ok(
        ApiResponse.ok(importedCount, "Successfully imported " + importedCount + " wards."));
  }
}
