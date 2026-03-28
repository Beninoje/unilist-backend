package com.unilist.campora.dto.reports;

import com.unilist.campora.utils.enums.ReportReason;
import com.unilist.campora.utils.enums.ReportStatus;
import com.unilist.campora.utils.enums.ReportTargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportDto{
        ReportTargetType targetType;
        UUID reporterId;
        UUID listingId;
        UUID targetUserId;
        ReportReason reason;
        String description;

}
