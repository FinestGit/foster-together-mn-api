package org.fostertogethermn.api.agency.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgencyCreateRequest(
        @NotBlank @Size(max = 255, message = "name cannot exceed 255 characters in length") String name) {
}
