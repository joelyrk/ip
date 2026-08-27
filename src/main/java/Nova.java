import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Starts the Nova chatbot application.
 */
public class Nova {
    private static final Storage STORAGE = new Storage(Path.of("data", "nova.txt"));
    private static final Ui UI = new Ui();

    public static void main(String[] args) {
        UI.showWelcome();

        ArrayList<Task> tasks;
        try {
            tasks = new ArrayList<>(STORAGE.load());
        } catch (NovaException e) {
            tasks = new ArrayList<>();
            UI.showError(e);
            UI.showSeparator();
        }

        while (UI.hasNextCommand()) {
            String command = UI.readCommand();
            UI.showSeparator();

            try {
                CommandType commandType = CommandType.from(command);
                switch (commandType) {
                case LIST:
                    UI.showTaskList(tasks);
                    break;
                case TODO:
                    addTask(parseTodo(command), tasks);
                    break;
                case DEADLINE:
                    addTask(parseDeadline(command), tasks);
                    break;
                case EVENT:
                    addTask(parseEvent(command), tasks);
                    break;
                case ON:
                    UI.showTasksOn(parseSearchDate(command), tasks);
                    break;
                case MARK:
                    int markIndex = parseTaskIndex(command, "mark", tasks.size());
                    updateTaskStatus(tasks.get(markIndex), true, tasks);
                    UI.showTaskMarked(tasks.get(markIndex));
                    break;
                case UNMARK:
                    int unmarkIndex = parseTaskIndex(command, "unmark", tasks.size());
                    updateTaskStatus(tasks.get(unmarkIndex), false, tasks);
                    UI.showTaskUnmarked(tasks.get(unmarkIndex));
                    break;
                case DELETE:
                    int deleteIndex = parseTaskIndex(command, "delete", tasks.size());
                    Task removedTask = deleteTask(deleteIndex, tasks);
                    UI.showTaskDeleted(removedTask, tasks.size());
                    break;
                case BYE:
                    UI.showGoodbye();
                    return;
                default:
                    throw new IllegalStateException("Unhandled command type: " + commandType);
                }
            } catch (NovaException e) {
                UI.showError(e);
            }
            UI.showSeparator();
        }
    }

    /**
     * Adds a task and prints the standard confirmation.
     *
     * @param task task to add
     * @param tasks list that stores Nova's tasks
     */
    private static void addTask(Task task, ArrayList<Task> tasks) throws NovaException {
        tasks.add(task);
        try {
            STORAGE.save(tasks);
        } catch (NovaException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
        UI.showTaskAdded(task, tasks.size());
    }

    /**
     * Changes a task's completion state and restores it if saving fails.
     *
     * @param task task whose status should change
     * @param isDone desired completion state
     * @param tasks complete task list to save
     * @throws NovaException if the updated list cannot be saved
     */
    private static void updateTaskStatus(Task task, boolean isDone, ArrayList<Task> tasks)
            throws NovaException {
        boolean previousStatus = task.isDone();
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }

        try {
            STORAGE.save(tasks);
        } catch (NovaException e) {
            if (previousStatus) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw e;
        }
    }

    /**
     * Deletes a task and reinserts it at the same position if saving fails.
     *
     * @param taskIndex zero-based index of the task to delete
     * @param tasks task list to update
     * @return the deleted task
     * @throws NovaException if the updated list cannot be saved
     */
    private static Task deleteTask(int taskIndex, ArrayList<Task> tasks) throws NovaException {
        Task removedTask = tasks.remove(taskIndex);
        try {
            STORAGE.save(tasks);
        } catch (NovaException e) {
            tasks.add(taskIndex, removedTask);
            throw e;
        }
        return removedTask;
    }

    /**
     * Creates a todo from a command after validating its description.
     *
     * @param command complete todo command
     * @return the parsed todo
     * @throws NovaException if the description is empty
     */
    private static Task parseTodo(String command) throws NovaException {
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
    private static Task parseDeadline(String command) throws NovaException {
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
    private static Task parseEvent(String command) throws NovaException {
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
     * Parses the date supplied to the stretch-goal search command.
     *
     * @param command complete {@code on} command
     * @return date whose scheduled tasks should be shown
     * @throws NovaException if no valid date follows {@code on}
     */
    private static LocalDate parseSearchDate(String command) throws NovaException {
        String dateText = command.substring("on".length()).trim();
        if (dateText.isEmpty()) {
            throw new NovaException("Tell me which date to search. Try: on 2019-12-02.");
        }
        return TaskDateTime.parseDate(dateText);
    }

    /**
     * Converts a task number in a mark, unmark, or delete command to a list index.
     *
     * @param command complete mark, unmark, or delete command
     * @param commandName command keyword used in error guidance
     * @param taskCount current number of tasks
     * @return zero-based index of the selected task
     * @throws NovaException if the task number is missing, invalid, or outside the list
     */
    private static int parseTaskIndex(String command, String commandName, int taskCount)
            throws NovaException {
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

        if (taskCount == 0) {
            throw new NovaException("There are no tasks to " + commandName + " yet. Add a task first.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new NovaException("Task " + taskNumber + " does not exist. Choose a number from 1 to "
                    + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Finds a command marker only when it appears as a separate token.
     *
     * @param text command arguments to search
     * @param marker marker such as {@code /by}, {@code /from}, or {@code /to}
     * @return the marker's index, or {@code -1} when it is absent
     */
    private static int findMarker(String text, String marker) {
        int markerIndex = text.indexOf(marker);
        while (markerIndex >= 0) {
            int afterMarker = markerIndex + marker.length();
            boolean hasLeftBoundary = markerIndex == 0 || Character.isWhitespace(text.charAt(markerIndex - 1));
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
