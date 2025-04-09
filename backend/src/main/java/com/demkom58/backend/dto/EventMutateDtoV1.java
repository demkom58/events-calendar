package com.demkom58.backend.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventMutateDtoV1 {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Start date/time is required")
    private LocalDateTime startDateTime;
    @NotNull(message = "End date/time is required")
    private LocalDateTime endDateTime;
    private String location;

    @AssertTrue(message = "End date/time must be after start date/time")
    private boolean isDateValid() {
        return startDateTime != null && endDateTime != null && endDateTime.isAfter(startDateTime);
    }
}
