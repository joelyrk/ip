import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Starts the Nova chatbot application.
 */
public class Nova {
    private static final Storage STORAGE = new Storage(Path.of("data", "nova.txt"));
    private static final Ui UI = new Ui();
    private static final Parser PARSER = new Parser();

    public static void main(String[] args) {
        UI.showWelcome();

        TaskList tasks;
        try {
            tasks = new TaskList(STORAGE.load());
        } catch (NovaException e) {
            tasks = new TaskList();
            UI.showError(e);
            UI.showSeparator();
        }

        while (UI.hasNextCommand()) {
            String command = UI.readCommand();
            UI.showSeparator();

            try {
                CommandType commandType = PARSER.parseCommandType(command);
                switch (commandType) {
                case LIST:
                    UI.showTaskList(tasks.getTasks());
                    break;
                case TODO:
                    addTask(PARSER.parseTodo(command), tasks);
                    break;
                case DEADLINE:
                    addTask(PARSER.parseDeadline(command), tasks);
                    break;
                case EVENT:
                    addTask(PARSER.parseEvent(command), tasks);
                    break;
                case ON:
                    LocalDate searchDate = PARSER.parseSearchDate(command);
                    UI.showTasksOn(searchDate, tasks.findTasksOn(searchDate));
                    break;
                case MARK:
                    int markIndex = PARSER.parseTaskIndex(command, "mark", tasks.size());
                    updateTaskStatus(markIndex, true, tasks);
                    UI.showTaskMarked(tasks.get(markIndex));
                    break;
                case UNMARK:
                    int unmarkIndex = PARSER.parseTaskIndex(command, "unmark", tasks.size());
                    updateTaskStatus(unmarkIndex, false, tasks);
                    UI.showTaskUnmarked(tasks.get(unmarkIndex));
                    break;
                case DELETE:
                    int deleteIndex = PARSER.parseTaskIndex(command, "delete", tasks.size());
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
    private static void addTask(Task task, TaskList tasks) throws NovaException {
        tasks.add(task);
        try {
            STORAGE.save(tasks.getTasks());
        } catch (NovaException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
        UI.showTaskAdded(task, tasks.size());
    }

    /**
     * Changes a task's completion state and restores it if saving fails.
     *
     * @param taskIndex zero-based index of the task whose status should change
     * @param isDone desired completion state
     * @param tasks complete task list to save
     * @throws NovaException if the updated list cannot be saved
     */
    private static void updateTaskStatus(int taskIndex, boolean isDone, TaskList tasks)
            throws NovaException {
        Task task = tasks.get(taskIndex);
        boolean previousStatus = task.isDone();
        if (isDone) {
            tasks.mark(taskIndex);
        } else {
            tasks.unmark(taskIndex);
        }

        try {
            STORAGE.save(tasks.getTasks());
        } catch (NovaException e) {
            if (previousStatus) {
                tasks.mark(taskIndex);
            } else {
                tasks.unmark(taskIndex);
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
    private static Task deleteTask(int taskIndex, TaskList tasks) throws NovaException {
        Task removedTask = tasks.remove(taskIndex);
        try {
            STORAGE.save(tasks.getTasks());
        } catch (NovaException e) {
            tasks.add(taskIndex, removedTask);
            throw e;
        }
        return removedTask;
    }

}
