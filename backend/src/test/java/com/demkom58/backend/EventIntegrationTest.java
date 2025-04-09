package com.demkom58.backend;

import com.demkom58.backend.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EventIntegrationTest {
    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:9.2")
            .withDatabaseName("calendar_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.properties.hibernate.jdbc.time_zone", () -> "UTC");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void Should_StoreEvent_When_EventPosted() {
        // Build a new Event payload
        Event newEvent = createTestEvent();

        // Set headers and create HttpEntity payload
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Event> request = new HttpEntity<>(newEvent, headers);

        // Send the POST request to create the event
        ResponseEntity<Event> postResponse = restTemplate.postForEntity("/events", request, Event.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK); // or HttpStatus.CREATED as appropriate
        Event createdEvent = postResponse.getBody();
        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getId()).isNotNull();

        // Send a GET request to fetch the event by its ID
        ResponseEntity<Event> getResponse = restTemplate.getForEntity("/events/" + createdEvent.getId(), Event.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Event fetchedEvent = getResponse.getBody();
        assertThat(fetchedEvent).isNotNull();
        assertThat(fetchedEvent.getTitle()).isEqualTo("Test Event");
        assertThat(fetchedEvent.getDescription()).isEqualTo("Test Description");
    }

    @Test
    void Should_UpdateEvent_When_EventPut() {
        // Create a new event first
        Event newEvent = createTestEvent();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Event> request = new HttpEntity<>(newEvent, headers);

        ResponseEntity<Event> postResponse = restTemplate.postForEntity("/events", request, Event.class);
        Event createdEvent = postResponse.getBody();

        // Update the event
        createdEvent.setTitle("Updated Title");
        HttpEntity<Event> updateRequest = new HttpEntity<>(createdEvent, headers);

        ResponseEntity<Event> putResponse = restTemplate.exchange("/events/" + createdEvent.getId(), HttpMethod.PUT, updateRequest, Event.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Event updatedEvent = putResponse.getBody();
        assertThat(updatedEvent).isNotNull();
        assertThat(updatedEvent.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void Should_DeleteEvent_When_EventDeleted() {
        // Create a new event first
        Event newEvent = createTestEvent();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Event> request = new HttpEntity<>(newEvent, headers);

        ResponseEntity<Event> postResponse = restTemplate.postForEntity("/events", request, Event.class);
        Event createdEvent = postResponse.getBody();

        // Delete the event
        restTemplate.delete("/events/" + createdEvent.getId());

        // Try to fetch the deleted event
        ResponseEntity<Event> getResponse = restTemplate.getForEntity("/events/" + createdEvent.getId(), Event.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void Should_ReturnAllEvents_When_GetAllEvents() {
        // Create a new event first
        Event newEvent = createTestEvent();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Event> request = new HttpEntity<>(newEvent, headers);

        restTemplate.postForEntity("/events", request, Event.class);

        // Fetch all events
        ResponseEntity<Event[]> getResponse = restTemplate.getForEntity("/events", Event[].class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Event[] events = getResponse.getBody();
        assertThat(events).isNotNull();
        assertThat(events.length).isGreaterThan(0);
    }

    @Test
    void Should_Return400_When_PostEventWithInvalidDates() {
        // Create an event with invalid dates
        Event invalidEvent = createTestEvent();
        invalidEvent.setEndDateTime(LocalDateTime.of(2025, 4, 9, 9, 0, 0)); // End date is before start date

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Event> request = new HttpEntity<>(invalidEvent, headers);

        // Send the POST request to create the event
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/events", request, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Should_Return400_When_UpdateEventWithInvalidDates() {
        // Create a new event first
        Event newEvent = createTestEvent();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Event> request = new HttpEntity<>(newEvent, headers);

        ResponseEntity<Event> postResponse = restTemplate.postForEntity("/events", request, Event.class);
        Event createdEvent = postResponse.getBody();

        // Update the event with invalid dates
        createdEvent.setEndDateTime(LocalDateTime.of(2025, 4, 9, 9, 0, 0)); // End date is before start date

        HttpEntity<Event> updateRequest = new HttpEntity<>(createdEvent, headers);

        ResponseEntity<String> putResponse = restTemplate.exchange("/events/" + createdEvent.getId(), HttpMethod.PUT, updateRequest, String.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Should_Return404_When_EventNotFound() {
        // Try to fetch a non-existing event
        ResponseEntity<Event> getResponse = restTemplate.getForEntity("/events/99999", Event.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void Should_Return400_When_NameIsEmpty() {
        // Create an event with an empty name
        Event invalidEvent = createTestEvent();
        invalidEvent.setTitle(""); // Empty title

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Event> request = new HttpEntity<>(invalidEvent, headers);

        // Send the POST request to create the event
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/events", request, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private static Event createTestEvent() {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setStartDateTime(LocalDateTime.of(2025, 4, 9, 10, 0, 0));
        event.setEndDateTime(LocalDateTime.of(2025, 4, 9, 12, 0, 0));
        event.setLocation("Test Location");
        return event;
    }


}
