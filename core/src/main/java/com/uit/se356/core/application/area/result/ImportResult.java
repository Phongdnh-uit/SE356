package com.uit.se356.core.application.area.result;

import java.util.List;

public record ImportResult(int imported, int skipped, int failed, List<String> errors) {}
