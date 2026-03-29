package org.fostertogethermn.api.agency.dto;

import java.time.OffsetDateTime;

public record AgencyResponse(
        Long id,
        String name,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}