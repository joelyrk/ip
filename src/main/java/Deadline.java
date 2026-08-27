import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a particular date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;
    private final boolean hasTime;

    /**
     * Creates an incomplete deadline with the given description and due time.
     *
     * @param description description of the deadline
     * @param by date and optional time by which the task must be completed
     * @param hasTime whether the due value includes an explicit time
     */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        this.by = by;
        this.hasTime = hasTime;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | "
                + TaskDateTime.formatForStorage(by, hasTime);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: "
                + TaskDateTime.formatForDisplay(by, hasTime) + ")";
    }
}
