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
        Task[] tasks = new Task[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            if (command.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("todo ")) {
                Task task = new Todo(command.substring(5).trim());
                tasks[taskCount] = task;
                taskCount++;
                printTaskAdded(task, taskCount);
            } else if (command.startsWith("deadline ") && command.contains(" /by ")) {
                int bySeparator = command.indexOf(" /by ");
                String description = command.substring(9, bySeparator).trim();
                String by = command.substring(bySeparator + 5).trim();
                Task task = new Deadline(description, by);
                tasks[taskCount] = task;
                taskCount++;
                printTaskAdded(task, taskCount);
            } else if (command.startsWith("event ")
                    && command.contains(" /from ") && command.contains(" /to ")) {
                int fromSeparator = command.indexOf(" /from ");
                int toSeparator = command.indexOf(" /to ", fromSeparator + 7);
                String description = command.substring(6, fromSeparator).trim();
                String from = command.substring(fromSeparator + 7, toSeparator).trim();
                String to = command.substring(toSeparator + 5).trim();
                Task task = new Event(description, from, to);
                tasks[taskCount] = task;
                taskCount++;
                printTaskAdded(task, taskCount);
            } else if (command.startsWith("mark ")) {
                try {
                    int taskNumber = Integer.parseInt(command.substring(5).trim());
                    int taskIndex = taskNumber - 1;

                    if (taskIndex < 0 || taskIndex >= taskCount) {
                        System.out.println(" Please enter the number of a task in your list.");
                    } else {
                        tasks[taskIndex].markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   " + tasks[taskIndex]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Please enter a valid task number after mark.");
                }
            } else if (command.startsWith("unmark ")) {
                try {
                    int taskNumber = Integer.parseInt(command.substring(7).trim());
                    int taskIndex = taskNumber - 1;

                    if (taskIndex < 0 || taskIndex >= taskCount) {
                        System.out.println(" Please enter the number of a task in your list.");
                    } else {
                        tasks[taskIndex].markAsNotDone();
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("   " + tasks[taskIndex]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Please enter a valid task number after unmark.");
                }
            } else {
                System.out.println(" Please enter a todo, deadline, or event command.");
            }
            System.out.println(separator);
        }
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
