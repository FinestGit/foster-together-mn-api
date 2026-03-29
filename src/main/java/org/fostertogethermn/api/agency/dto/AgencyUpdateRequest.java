package org.fostertogethermn.api.agency.dto;

import jakarta.validation.constraints.NotBlank;

public record AgencyUpdateRequest(
        @NotBlank String name) {
}
