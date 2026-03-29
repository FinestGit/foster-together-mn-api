package org.fostertogethermn.api.agency.dto;

import jakarta.validation.constraints.NotBlank;

public record AgencyCreateRequest(
        @NotBlank String name) {
}
