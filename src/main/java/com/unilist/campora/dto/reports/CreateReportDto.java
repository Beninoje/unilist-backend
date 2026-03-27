package com.unilist.campora.dto.reports;

import com.unilist.campora.utils.enums.ReportReason;
import com.unilist.campora.utils.enums.ReportStatus;

import java.util.UUID;

public record CreateReportDto(
        UUID reporterId,
        UUID listingId,
        ReportReason reason,
        String description
) {
}
