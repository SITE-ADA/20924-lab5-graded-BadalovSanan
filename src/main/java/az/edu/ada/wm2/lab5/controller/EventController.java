package az.edu.ada.wm2.lab5.controller;

import az.edu.ada.wm2.lab5.model.Event;
import az.edu.ada.wm2.lab5.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 1. CREATE - POST /api/events
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        try {
            Event createdEvent = eventService.createEvent(event);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 2. LIST ALL - GET /api/events
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        try {
            List<Event> events = eventService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. GET ONE BY ID - GET /api/events/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable UUID id) {
        try {
            Event event = eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 4. REMOVE BY ID - DELETE /api/events/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        try {
            eventService.deleteEvent(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. FULL UPDATE (PUT) - PUT /api/events/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable UUID id, @RequestBody Event event) {
        try {
            Event updatedEvent = eventService.updateEvent(id, event);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 6. PARTIAL UPDATE (PATCH) - PATCH /api/events/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Event> partialUpdateEvent(@PathVariable UUID id, @RequestBody Event partialEvent) {
        try {
            Event updatedEvent = eventService.partialUpdateEvent(id, partialEvent);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // PATCH /{id}/price
    @PatchMapping("/{id}/price")
    public ResponseEntity<Event> updatePrice(@PathVariable UUID id,
                                            @RequestParam Double price) {
        try {
            if (price == null || price < 0) return ResponseEntity.badRequest().build();
            Event updated = eventService.updateEventPrice(id, BigDecimal.valueOf(price));
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /upcoming
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        try {
            List<Event> upcoming = eventService.getUpcomingEvents();
            return ResponseEntity.ok(upcoming);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /filter/tag
    @GetMapping("/filter/tag")
    public ResponseEntity<List<Event>> getEventsByTag(@RequestParam String tag) {
        try {
            List<Event> events = eventService.getEventsByTag(tag);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /filter/price
    @GetMapping("/filter/price")
    public ResponseEntity<List<Event>> getEventsByPrice(@RequestParam Double min,
                                                        @RequestParam Double max) {
        try {
            if (min == null || max == null) return ResponseEntity.badRequest().build();
            List<Event> events = eventService.getEventsByPriceRange(BigDecimal.valueOf(min), BigDecimal.valueOf(max));
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /filter/date
    @GetMapping("/filter/date")
    public ResponseEntity<List<Event>> getEventsByDate(@RequestParam String start,
                                                       @RequestParam String end) {
        try {
            LocalDateTime startTime = LocalDateTime.parse(start);
            LocalDateTime endTime = LocalDateTime.parse(end);
            List<Event> events = eventService.getEventsByDateRange(startTime, endTime);
            return ResponseEntity.ok(events);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}