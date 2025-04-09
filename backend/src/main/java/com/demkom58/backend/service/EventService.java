package com.demkom58.backend.service;

import com.demkom58.backend.dto.EventMapper;
import com.demkom58.backend.dto.EventMutateDtoV1;
import com.demkom58.backend.model.Event;
import com.demkom58.backend.repository.EventRepository;
import com.demkom58.backend.service.exceptions.InvalidInputDataException;
import com.demkom58.backend.service.exceptions.ItemNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    public Event createEvent(EventMutateDtoV1 eventMutateDto) throws InvalidInputDataException {
        Event event = new Event();
        eventMapper.mutate(eventMutateDto, event);
        return eventRepository.save(event);
    }

    public List<Event> listEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEvent(Long id) {
        return eventRepository.findById(id);
    }

    public Event updateEvent(Long id, EventMutateDtoV1 newData) throws ItemNotFoundException {
        Event existing = getEvent(id)
                .orElseThrow(() -> new ItemNotFoundException("Event with id " + id + " not found"));

        eventMapper.mutate(newData, existing);
        return eventRepository.save(existing);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
