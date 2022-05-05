package com.sobkolex.account_service.services;

import com.sobkolex.account_service.entity.Event;
import com.sobkolex.account_service.model.SecurityEvent;
import com.sobkolex.account_service.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private EventRepository eventRepository;

    public void save(SecurityEvent securityEvent, String subject, String object, String path) {
        Event event = new Event();
        event.setDate(LocalDateTime.now());
        event.setPath(path);
        event.setObject(object);
        event.setSubject(subject == null ? "Anonymous" : subject);
        event.setAction(securityEvent);
        eventRepository.save(event);
    }

    public List<Event> findAllEvents() {
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
}
