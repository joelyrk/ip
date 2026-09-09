package nova.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import nova.exception.NovaException;
import nova.task.Task;
import nova.task.TaskList;

/**
 * Handles Nova's console input and output.
 */
public class Ui {
    private static final String DISPLAY_SEPARATOR = "_".repeat(60);
    private static final String DISPLAY_BANNER = " _   _                 \n"
            + "| \\ | | _____   ____ _ \n"
            + "|  \\| |/ _ \\ \\ / / _` |\n"
            + "| |\\  | (_) \\ V / (_| |\n"
            + "|_| \\_|\\___/ \\_/ \\__,_|\n";
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu");

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a console UI that reads commands from standard input.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates an output-only UI for presenting a command result.
     *
     * @param output destination for displayed text.
     */
    public Ui(PrintStream output) {
        this(InputStream.nullInputStream(), output);
    }

    /**
     * Creates a UI using the supplied input and output streams.
     *
     * @param input source of user commands.
     * @param output destination for displayed text.
     */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Displays Nova's greeting when the application starts.
     */
    public void showWelcome() {
        output.println(DISPLAY_SEPARATOR);
        output.print(DISPLAY_BANNER);
        output.println("Hello! I'm Nova.");
        output.println("What can I do for you?");
        output.println(DISPLAY_SEPARATOR);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} when another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return the next command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the line used to separate command responses.
     */
    public void showSeparator() {
        output.println(DISPLAY_SEPARATOR);
    }

    /**
     * Displays a user-friendly error message.
     *
     * @param error error to explain to the user.
     */
    public void showError(NovaException error) {
        output.println(" OOPS!!! " + error.getMessage());
    }

    /**
     * Displays all tasks in their current list order.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        output.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays the confirmation shown after a task is added.
     *
     * @param task newly added task.
     * @param taskCount total number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println(" Got it. I've added this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the confirmation shown after a task is marked as done.
     *
     * @param task task that was marked.
     */
    public void showTaskMarked(Task task) {
        output.println(" Nice! I've marked this task as done:");
        output.println("   " + task);
    }

    /**
     * Displays the confirmation shown after a task is marked as not done.
     *
     * @param task task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        output.println(" OK, I've marked this task as not done yet:");
        output.println("   " + task);
    }

    /**
     * Displays the confirmation shown after a task is deleted.
     *
     * @param task deleted task.
     * @param taskCount total number of remaining tasks.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(" Noted. I've removed this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays scheduled tasks occurring on a date while retaining their original numbers.
     *
     * @param date date being searched.
     * @param tasks matching tasks with their original task numbers.
     */
    public void showTasksOn(LocalDate date, List<TaskList.NumberedTask> tasks) {
        output.println(" Here are the tasks occurring on " + date.format(DISPLAY_DATE_FORMATTER) + ":");
        showNumberedTasks(tasks);
        if (tasks.isEmpty()) {
            output.println(" No deadlines or events occur on this date.");
        }
    }

    /**
     * Displays tasks with descriptions matching a keyword while retaining their original numbers.
     *
     * @param tasks matching tasks with their original task numbers.
     */
    public void showMatchingTasks(List<TaskList.NumberedTask> tasks) {
        output.println(" Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
        if (tasks.isEmpty()) {
            output.println(" No matching tasks found.");
        }
    }

    /**
     * Displays tasks using their original one-based numbers.
     *
     * @param tasks numbered tasks to display.
     */
    private void showNumberedTasks(List<TaskList.NumberedTask> tasks) {
        for (TaskList.NumberedTask numberedTask : tasks) {
            output.println(" " + numberedTask.number() + "." + numberedTask.task());
        }
    }

    /**
     * Displays Nova's farewell message.
     */
    public void showGoodbye() {
        output.println(" Bye. Hope to see you again soon!");
    }
}
