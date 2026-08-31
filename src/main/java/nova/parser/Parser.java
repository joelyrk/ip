package nova.parser;

import java.time.LocalDate;

import nova.command.AddCommand;
import nova.command.Command;
import nova.command.DeleteCommand;
import nova.command.ExitCommand;
import nova.command.FindCommand;
import nova.command.ListCommand;
import nova.command.MarkCommand;
import nova.command.UnmarkCommand;
import nova.exception.NovaException;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.TaskDateTime;
import nova.task.Todo;

/**
 * Interprets and validates commands entered by the user.
 */
public class Parser {
    /**
     * Converts a complete line of user input into an executable command.
     *
     * @param fullCommand complete command entered by the user.
     * @return command containing the parsed arguments.
     * @throws NovaException if the command or any of its arguments is invalid.
     */
    public Command parse(String fullCommand) throws NovaException {
        CommandType commandType = parseCommandType(fullCommand);
        return switch (commandType) {
            case LIST -> new ListCommand();
            case TODO -> new AddCommand(parseTodo(fullCommand));
            case DEADLINE -> new AddCommand(parseDeadline(fullCommand));
            case EVENT -> new AddCommand(parseEvent(fullCommand));
            case ON -> new FindCommand(parseSearchDate(fullCommand));
            case MARK -> new MarkCommand(parseTaskNumber(fullCommand, "mark"));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(fullCommand, "unmark"));
            case DELETE -> new DeleteCommand(parseTaskNumber(fullCommand, "delete"));
            case BYE -> new ExitCommand();
        };
    }

    /**
     * Identifies the type of a complete command.
     *
     * @param command trimmed command entered by the user.
     * @return matching command type.
     * @throws NovaException if the command is blank or has an unknown form.
     */
    private CommandType parseCommandType(String command) throws NovaException {
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
     * @param command complete todo command.
     * @return the parsed todo.
     * @throws NovaException if the description is empty.
     */
    private Task parseTodo(String command) throws NovaException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("A todo needs a description. Try: todo <description>.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline from a command after validating its description and due time.
     *
     * @param command complete deadline command.
     * @return the parsed deadline.
     * @throws NovaException if a required deadline field is missing.
     */
    private Task parseDeadline(String command) throws NovaException {
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
        String dueDateTimeText = arguments.substring(bySeparator + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("A deadline needs a description before /by.");
        }
        if (dueDateTimeText.isEmpty()) {
            throw new NovaException("The /by field cannot be empty. Add a date or time after /by.");
        }

        TaskDateTime.ParsedValue dueDateTime = TaskDateTime.parse(dueDateTimeText, "/by");
        return new Deadline(description, dueDateTime.dateTime(), dueDateTime.hasTime());
    }

    /**
     * Creates an event from a command after validating its description and time range.
     *
     * @param command complete event command.
     * @return the parsed event.
     * @throws NovaException if a required event field is missing or out of order.
     */
    private Task parseEvent(String command) throws NovaException {
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
        String startDateTimeText = arguments.substring(fromSeparator + "/from".length(), toSeparator).trim();
        String endDateTimeText = arguments.substring(toSeparator + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("An event needs a description before /from.");
        }
        if (startDateTimeText.isEmpty()) {
            throw new NovaException("The /from field cannot be empty. Add a start date or time after /from.");
        }
        if (endDateTimeText.isEmpty()) {
            throw new NovaException("The /to field cannot be empty. Add an end date or time after /to.");
        }

        TaskDateTime.ParsedValue start = TaskDateTime.parse(startDateTimeText, "/from");
        TaskDateTime.ParsedValue end = TaskDateTime.parse(endDateTimeText, "/to");
        if (end.dateTime().isBefore(start.dateTime())) {
            throw new NovaException("An event's /to date/time cannot be before its /from date/time.");
        }
        return new Event(description, start.dateTime(), start.hasTime(),
                end.dateTime(), end.hasTime());
    }

    /**
     * Parses the date supplied to the date-search command.
     *
     * @param command complete {@code on} command.
     * @return date whose scheduled tasks should be shown.
     * @throws NovaException if no valid date follows {@code on}.
     */
    private LocalDate parseSearchDate(String command) throws NovaException {
        String dateText = command.substring("on".length()).trim();
        if (dateText.isEmpty()) {
            throw new NovaException("Tell me which date to search. Try: on 2019-12-02.");
        }
        return TaskDateTime.parseDate(dateText);
    }

    /**
     * Parses the task number in a mark, unmark, or delete command.
     *
     * @param command complete mark, unmark, or delete command.
     * @param commandName command keyword used in error guidance.
     * @return one-based task number entered by the user.
     * @throws NovaException if the task number is missing or is not a whole number.
     */
    private int parseTaskNumber(String command, String commandName) throws NovaException {
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
     * @param text command arguments to search.
     * @param marker marker such as {@code /by}, {@code /from}, or {@code /to}.
     * @return the marker's index, or {@code -1} when it is absent.
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
