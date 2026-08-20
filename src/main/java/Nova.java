import java.util.ArrayList;
import java.util.Scanner;

/**
 * Starts the Nova chatbot application.
 */
public class Nova {
    public static void main(String[] args) {
        String separator = "_".repeat(60);
        String banner = " _   _                 \n"
                + "| \\ | | _____   ____ _ \n"
                + "|  \\| |/ _ \\ \\ / / _` |\n"
                + "| |\\  | (_) \\ V / (_| |\n"
                + "|_| \\_|\\___/ \\_/ \\__,_|\n";

        System.out.println(separator);
        System.out.print(banner);
        System.out.println("Hello! I'm Nova.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            System.out.println(separator);

            try {
                CommandType commandType = CommandType.from(command);
                switch (commandType) {
                case LIST:
                    printTaskList(tasks);
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
                case MARK:
                    int markIndex = parseTaskIndex(command, "mark", tasks.size());
                    tasks.get(markIndex).markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(markIndex));
                    break;
                case UNMARK:
                    int unmarkIndex = parseTaskIndex(command, "unmark", tasks.size());
                    tasks.get(unmarkIndex).markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(unmarkIndex));
                    break;
                case DELETE:
                    int deleteIndex = parseTaskIndex(command, "delete", tasks.size());
                    Task removedTask = tasks.remove(deleteIndex);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    break;
                case BYE:
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    return;
                default:
                    throw new IllegalStateException("Unhandled command type: " + commandType);
                }
            } catch (NovaException e) {
                System.out.println(" OOPS!!! " + e.getMessage());
            }
            System.out.println(separator);
        }
    }

    /**
     * Adds a task and prints the standard confirmation.
     *
     * @param task task to add
     * @param tasks list that stores Nova's tasks
     */
    private static void addTask(Task task, ArrayList<Task> tasks) {
        tasks.add(task);
        printTaskAdded(task, tasks.size());
    }

    /**
     * Prints all tasks in their current list order.
     *
     * @param tasks tasks to display
     */
    private static void printTaskList(ArrayList<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
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
        return new Deadline(description, by);
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
        return new Event(description, from, to);
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

    /**
     * Prints the confirmation shown after a task is added.
     *
     * @param task newly added task
     * @param taskCount total number of tasks after the addition
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }
}
