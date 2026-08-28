import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Coordinates Nova's storage, task list, parser, and user interface.
 */
public class Nova {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private final NovaException loadingError;

    /**
     * Creates Nova and loads its saved tasks.
     * A loading error is retained so it can be shown after the welcome message.
     *
     * @param filePath path of Nova's task data file
     */
    public Nova(String filePath) {
        storage = new Storage(Path.of(filePath));
        ui = new Ui();
        parser = new Parser();

        TaskList loadedTasks;
        NovaException startupError = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (NovaException e) {
            loadedTasks = new TaskList();
            startupError = e;
        }
        tasks = loadedTasks;
        loadingError = startupError;
    }

    /**
     * Runs Nova's command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showError(loadingError);
            ui.showSeparator();
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();

            try {
                CommandType commandType = parser.parseCommandType(command);
                switch (commandType) {
                case LIST:
                    ui.showTaskList(tasks.getTasks());
                    break;
                case TODO:
                    addTask(parser.parseTodo(command));
                    break;
                case DEADLINE:
                    addTask(parser.parseDeadline(command));
                    break;
                case EVENT:
                    addTask(parser.parseEvent(command));
                    break;
                case ON:
                    LocalDate searchDate = parser.parseSearchDate(command);
                    ui.showTasksOn(searchDate, tasks.findTasksOn(searchDate));
                    break;
                case MARK:
                    int markIndex = parser.parseTaskIndex(command, "mark", tasks.size());
                    updateTaskStatus(markIndex, true);
                    ui.showTaskMarked(tasks.get(markIndex));
                    break;
                case UNMARK:
                    int unmarkIndex = parser.parseTaskIndex(command, "unmark", tasks.size());
                    updateTaskStatus(unmarkIndex, false);
                    ui.showTaskUnmarked(tasks.get(unmarkIndex));
                    break;
                case DELETE:
                    int deleteIndex = parser.parseTaskIndex(command, "delete", tasks.size());
                    Task removedTask = deleteTask(deleteIndex);
                    ui.showTaskDeleted(removedTask, tasks.size());
                    break;
                case BYE:
                    ui.showGoodbye();
                    return;
                default:
                    throw new IllegalStateException("Unhandled command type: " + commandType);
                }
            } catch (NovaException e) {
                ui.showError(e);
            }
            ui.showSeparator();
        }
    }

    /**
     * Starts Nova using its default relative data-file path.
     *
     * @param args command-line arguments, which Nova does not currently use
     */
    public static void main(String[] args) {
        new Nova("data/nova.txt").run();
    }

    /**
     * Adds a task and prints the standard confirmation.
     *
     * @param task task to add
     */
    private void addTask(Task task) throws NovaException {
        tasks.add(task);
        try {
            storage.save(tasks.getTasks());
        } catch (NovaException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Changes a task's completion state and restores it if saving fails.
     *
     * @param taskIndex zero-based index of the task whose status should change
     * @param isDone desired completion state
     * @throws NovaException if the updated list cannot be saved
     */
    private void updateTaskStatus(int taskIndex, boolean isDone) throws NovaException {
        Task task = tasks.get(taskIndex);
        boolean previousStatus = task.isDone();
        if (isDone) {
            tasks.mark(taskIndex);
        } else {
            tasks.unmark(taskIndex);
        }

        try {
            storage.save(tasks.getTasks());
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
     * @return the deleted task
     * @throws NovaException if the updated list cannot be saved
     */
    private Task deleteTask(int taskIndex) throws NovaException {
        Task removedTask = tasks.remove(taskIndex);
        try {
            storage.save(tasks.getTasks());
        } catch (NovaException e) {
            tasks.add(taskIndex, removedTask);
            throw e;
        }
        return removedTask;
    }
}
