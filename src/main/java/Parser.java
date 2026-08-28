import java.time.LocalDate;

/**
 * Interprets and validates commands entered by the user.
 */
public class Parser {
    /**
     * Identifies the type of a complete command.
     *
     * @param command trimmed command entered by the user
     * @return matching command type
     * @throws NovaException if the command is blank or has an unknown form
     */
    public CommandType parseCommandType(String command) throws NovaException {
        if (command.isEmpty()) {
            throw new NovaException("You entered a blank command. Try todo, deadline, event, on, list, mark, "
                    + "unmark, delete, or bye.");
        }

        for (CommandType commandType : CommandType.values()) {
            if (commandType.matches(command)) {
                return commandType;
            }
        }

        throw new NovaException("I don't recognize that command. Start with todo, deadline, event, on, "
                + "list, mark, unmark, delete, or bye.");
    }

    /**
     * Creates a todo from a command after validating its description.
     *
     * @param command complete todo command
     * @return the parsed todo
     * @throws NovaException if the description is empty
     */
    public Task parseTodo(String command) throws NovaException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("A todo needs a description. Try: todo <description>.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline from a command after validating its description and due time.
     *
     * @param command complete deadline command
     * @return the parsed deadline
     * @throws NovaException if a required deadline field is missing
     */
    public Task parseDeadline(String command) throws NovaException {
        String arguments = command.substring("deadline".length()).trim();
        if (arguments.isEmpty()) {
            throw new NovaException("A deadline needs a description. "
                    + "Try: deadline <description> /by <date or time>.");
        }

        int bySeparator = findMarker(arguments, "/by");
        if (bySeparator < 0) {
            throw new NovaException("A deadline needs a /by date or time. "
                    + "Try: deadline " + arguments + " /by <date or time>.");
        }

        String description = arguments.substring(0, bySeparator).trim();
        String by = arguments.substring(bySeparator + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("A deadline needs a description before /by.");
        }
        if (by.isEmpty()) {
            throw new NovaException("The /by field cannot be empty. Add a date or time after /by.");
        }
        TaskDateTime.ParsedValue dueDateTime = TaskDateTime.parse(by, "/by");
        return new Deadline(description, dueDateTime.dateTime(), dueDateTime.hasTime());
    }

    /**
     * Creates an event from a command after validating its description and time range.
     *
     * @param command complete event command
     * @return the parsed event
     * @throws NovaException if a required event field is missing or out of order
     */
    public Task parseEvent(String command) throws NovaException {
        String arguments = command.substring("event".length()).trim();
        if (arguments.isEmpty()) {
            throw new NovaException("An event needs a description and a time range. "
                    + "Try: event <description> /from <start> /to <end>.");
        }

        int fromSeparator = findMarker(arguments, "/from");
        int toSeparator = findMarker(arguments, "/to");
        if (fromSeparator < 0) {
            throw new NovaException("An event needs a /from start date or time.");
        }
        if (toSeparator < 0) {
            throw new NovaException("An event needs a /to end date or time.");
        }
        if (toSeparator < fromSeparator) {
            throw new NovaException("Put /from before /to. "
                    + "Try: event <description> /from <start> /to <end>.");
        }

        String description = arguments.substring(0, fromSeparator).trim();
        String from = arguments.substring(fromSeparator + "/from".length(), toSeparator).trim();
        String to = arguments.substring(toSeparator + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("An event needs a description before /from.");
        }
        if (from.isEmpty()) {
            throw new NovaException("The /from field cannot be empty. Add a start date or time after /from.");
        }
        if (to.isEmpty()) {
            throw new NovaException("The /to field cannot be empty. Add an end date or time after /to.");
        }
        TaskDateTime.ParsedValue start = TaskDateTime.parse(from, "/from");
        TaskDateTime.ParsedValue end = TaskDateTime.parse(to, "/to");
        if (end.dateTime().isBefore(start.dateTime())) {
            throw new NovaException("An event's /to date/time cannot be before its /from date/time.");
        }
        return new Event(description, start.dateTime(), start.hasTime(),
                end.dateTime(), end.hasTime());
    }

    /**
     * Parses the date supplied to the date-search command.
     *
     * @param command complete {@code on} command
     * @return date whose scheduled tasks should be shown
     * @throws NovaException if no valid date follows {@code on}
     */
    public LocalDate parseSearchDate(String command) throws NovaException {
        String dateText = command.substring("on".length()).trim();
        if (dateText.isEmpty()) {
            throw new NovaException("Tell me which date to search. Try: on 2019-12-02.");
        }
        return TaskDateTime.parseDate(dateText);
    }

    /**
     * Parses the task number in a mark, unmark, or delete command.
     *
     * @param command complete mark, unmark, or delete command
     * @param commandName command keyword used in error guidance
     * @return one-based task number entered by the user
     * @throws NovaException if the task number is missing or is not a whole number
     */
    public int parseTaskNumber(String command, String commandName) throws NovaException {
        String taskNumberText = command.substring(commandName.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new NovaException("Tell me which task to " + commandName
                    + ". Try: " + commandName + " <task number>.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new NovaException("The task number after " + commandName
                    + " must be a whole number, for example: " + commandName + " 1.");
        }

        return taskNumber;
    }

    /**
     * Finds a command marker only when it appears as a separate token.
     *
     * @param text command arguments to search
     * @param marker marker such as {@code /by}, {@code /from}, or {@code /to}
     * @return the marker's index, or {@code -1} when it is absent
     */
    private int findMarker(String text, String marker) {
        int markerIndex = text.indexOf(marker);
        while (markerIndex >= 0) {
            int afterMarker = markerIndex + marker.length();
            boolean hasLeftBoundary = markerIndex == 0
                    || Character.isWhitespace(text.charAt(markerIndex - 1));
            boolean hasRightBoundary = afterMarker == text.length()
                    || Character.isWhitespace(text.charAt(afterMarker));
            if (hasLeftBoundary && hasRightBoundary) {
                return markerIndex;
            }
            markerIndex = text.indexOf(marker, markerIndex + 1);
        }
        return -1;
    }
}
