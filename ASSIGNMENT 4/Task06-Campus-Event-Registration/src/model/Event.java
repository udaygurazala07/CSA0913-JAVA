package task06.model;

/**
 * Model representing a campus event.
 */
public class Event {
    private int id;
    private String title;
    private String category;
    private String eventDate;
    private String venue;
    private int capacity;

    public Event(int id, String title, String category, String eventDate, String venue, int capacity) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.eventDate = eventDate;
        this.venue = venue;
        this.capacity = capacity;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getEventDate() { return eventDate; }
    public String getVenue() { return venue; }
    public int getCapacity() { return capacity; }

    @Override
    public String toString() {
        return title + " [" + category + "]";
    }
}