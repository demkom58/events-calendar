package com.demkom58.backend.model;

import jakarta.persistence.*;
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

    /**
     * The end date/time of the event. It must be after the start date/time.
     */
    @NotNull(message = "End date/time is required")
    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    private String location;
}
