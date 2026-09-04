package com.digitalbank.corebanking.common;

import java.time.OffsetDateTime;
public record ErrorResponse(boolean success, String message, String errorCode, OffsetDateTime timestamp) {}
