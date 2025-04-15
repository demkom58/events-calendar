package com.demkom58.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class EventResponseDtoV1 {
    private Long id;
    private String title;
    private String description;
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    private String location;
}
