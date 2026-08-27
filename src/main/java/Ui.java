import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Handles Nova's console input and output.
 */
public class Ui {
    private static final String SEPARATOR = "_".repeat(60);
    private static final String BANNER = " _   _                 \n"
            + "| \\ | | _____   ____ _ \n"
            + "|  \\| |/ _ \\ \\ / / _` |\n"
            + "| |\\  | (_) \\ V / (_| |\n"
            + "|_| \\_|\\___/ \\_/ \\__,_|\n";
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu");

    private final Scanner scanner;

    /**
     * Creates a console UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Nova's greeting when the application starts.
     */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.print(BANNER);
        System.out.println("Hello! I'm Nova.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} when another input line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return the next command
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the line used to separate command responses.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays a user-friendly error message.
     *
     * @param error error to explain to the user
     */
    public void showError(NovaException error) {
        System.out.println(" OOPS!!! " + error.getMessage());
    }

    /**
     * Displays all tasks in their current list order.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays the confirmation shown after a task is added.
     *
     * @param task newly added task
     * @param taskCount total number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the confirmation shown after a task is marked as done.
     *
     * @param task task that was marked
     */
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    /**
     * Displays the confirmation shown after a task is marked as not done.
     *
     * @param task task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /**
     * Displays the confirmation shown after a task is deleted.
     *
     * @param task deleted task
     * @param taskCount total number of remaining tasks
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays scheduled tasks occurring on a date while retaining their original numbers.
     *
     * @param date date being searched
     * @param tasks complete task list
     */
    public void showTasksOn(LocalDate date, List<Task> tasks) {
        System.out.println(" Here are the tasks occurring on " + date.format(DISPLAY_DATE) + ":");
        boolean foundTask = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                System.out.println(" " + (i + 1) + "." + tasks.get(i));
                foundTask = true;
            }
        }
        if (!foundTask) {
            System.out.println(" No deadlines or events occur on this date.");
        }
    }

    /**
     * Displays Nova's farewell.
     */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }
}
