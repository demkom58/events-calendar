package com.demkom58.backend.controller;

import com.demkom58.backend.dto.EventMutateDtoV1;
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

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public Event addEvent(@Valid @RequestBody EventMutateDtoV1 event) {
        return eventService.createEvent(event);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.listEvents();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEvent(id)
                .orElseThrow(() -> new ItemNotFoundException("Event with id " + id + " not found"));
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @Valid @RequestBody EventMutateDtoV1 event) {
        return eventService.updateEvent(id, event);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
