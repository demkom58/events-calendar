package com.demkom58.backend.dto;

import com.demkom58.backend.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public void mutate(EventMutateDtoV1 mutateDto, Event event) {
        event.setTitle(mutateDto.getTitle());
        event.setDescription(mutateDto.getDescription());
        event.setStartDateTime(mutateDto.getStartDateTime());
        event.setEndDateTime(mutateDto.getEndDateTime());
        event.setLocation(mutateDto.getLocation());
    }
}
