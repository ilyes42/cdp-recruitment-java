package adeo.leroymerlin.cdp;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
        eventRepository.flush();
    }

    public List<Event> getFilteredEvents(String query) {
        List<Event> events = eventRepository.findAll();
        // Filter the events list in pure JAVA here
        return events.stream().filter(event -> event.doesMatchQuery(query)).peek(event -> {
            int howManyBands = event.getBands().size();
            String newEventTitle = event.getTitle().concat(" [" + String.valueOf(howManyBands) + ']');
            event.setTitle(newEventTitle);
            var newBands = event.getBands().stream().peek(band -> {
                int howManyMembers = band.getMembers().size();
                String newBandName = band.getName().concat(" [" + String.valueOf(howManyMembers) + ']');
                band.setName(newBandName);
            }).collect(Collectors.toSet());
            event.setBands(newBands);
        }).toList();
    }

    public void updateEvent(Long id, Event newEvent) {
        Event existingEvent = eventRepository.getReferenceById(id);
        existingEvent.setNbStars(newEvent.getNbStars());
        existingEvent.setComment(newEvent.getComment());
        eventRepository.save(existingEvent);
        eventRepository.flush();
    }
}
