package nova.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task that takes place between a start and an end date or time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final boolean fromHasTime;
    private final LocalDateTime to;
    private final boolean toHasTime;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description description of the event
     * @param from date and optional time at which the event starts
     * @param fromHasTime whether the start includes an explicit time
     * @param to date and optional time at which the event ends
     * @param toHasTime whether the end includes an explicit time
     */
    public Event(String description, LocalDateTime from, boolean fromHasTime,
            LocalDateTime to, boolean toHasTime) {
        super(description);
        this.from = from;
        this.fromHasTime = fromHasTime;
        this.to = to;
        this.toHasTime = toHasTime;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = from.toLocalDate();
        LocalDate endDate = to.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | "
                + TaskDateTime.formatForStorage(from, fromHasTime)
                + " | " + TaskDateTime.formatForStorage(to, toHasTime);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: "
                + TaskDateTime.formatForDisplay(from, fromHasTime)
                + " to: " + TaskDateTime.formatForDisplay(to, toHasTime) + ")";
    }
}
