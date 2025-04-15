package com.demkom58.backend.controller;

import com.demkom58.backend.dto.EventMapper;
import com.demkom58.backend.dto.EventMutateDtoV1;
import com.demkom58.backend.dto.EventResponseDtoV1;
import com.demkom58.backend.model.Event;
import com.demkom58.backend.service.EventService;
import com.demkom58.backend.service.exceptions.ItemNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@CrossOrigin
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @PostMapping
    public EventResponseDtoV1 addEvent(@Valid @RequestBody EventMutateDtoV1 event) {
        return eventMapper.toResponseDto(eventService.createEvent(event));
    }

    @GetMapping
    public List<EventResponseDtoV1> getAllEvents() {
        return eventService.listEvents().stream().map(eventMapper::toResponseDto).toList();
    }

    @GetMapping("/{id}")
    public EventResponseDtoV1 getEventById(@PathVariable Long id) {
        return eventService.getEvent(id)
                .map(eventMapper::toResponseDto)
                .orElseThrow(() -> new ItemNotFoundException("Event with id " + id + " not found"));
    }

    @PutMapping("/{id}")
    public EventResponseDtoV1 updateEvent(@PathVariable Long id, @Valid @RequestBody EventMutateDtoV1 event) {
        return eventMapper.toResponseDto(eventService.updateEvent(id, event));
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
