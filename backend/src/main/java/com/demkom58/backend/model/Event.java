package com.demkom58.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Start date/time is required")
    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date/time is required")
    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    private String location;

    @PrePersist @PreUpdate
    private void validateDates() {
        if (!isDateValid()) throw new IllegalArgumentException("End date/time must be after start date/time");

    }

    @AssertTrue(message = "End date/time must be after start date/time")
    private boolean isDateValid() {
        return startDateTime != null && endDateTime != null && endDateTime.isAfter(startDateTime);
    }
}
