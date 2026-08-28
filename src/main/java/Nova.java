import java.nio.file.Path;

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

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();

            try {
                CommandType commandType = parser.parseCommandType(command);
                switch (commandType) {
                case LIST:
                    Command listCommand = new ListCommand();
                    listCommand.execute(tasks, ui, storage);
                    break;
                case TODO:
                    Command todoCommand = new AddCommand(parser.parseTodo(command));
                    todoCommand.execute(tasks, ui, storage);
                    break;
                case DEADLINE:
                    Command deadlineCommand = new AddCommand(parser.parseDeadline(command));
                    deadlineCommand.execute(tasks, ui, storage);
                    break;
                case EVENT:
                    Command eventCommand = new AddCommand(parser.parseEvent(command));
                    eventCommand.execute(tasks, ui, storage);
                    break;
                case ON:
                    Command findCommand = new FindCommand(parser.parseSearchDate(command));
                    findCommand.execute(tasks, ui, storage);
                    break;
                case MARK:
                    int markIndex = parser.parseTaskIndex(command, "mark", tasks.size());
                    Command markCommand = new MarkCommand(markIndex);
                    markCommand.execute(tasks, ui, storage);
                    break;
                case UNMARK:
                    int unmarkIndex = parser.parseTaskIndex(command, "unmark", tasks.size());
                    Command unmarkCommand = new UnmarkCommand(unmarkIndex);
                    unmarkCommand.execute(tasks, ui, storage);
                    break;
                case DELETE:
                    int deleteIndex = parser.parseTaskIndex(command, "delete", tasks.size());
                    Command deleteCommand = new DeleteCommand(deleteIndex);
                    deleteCommand.execute(tasks, ui, storage);
                    break;
                case BYE:
                    Command exitCommand = new ExitCommand();
                    exitCommand.execute(tasks, ui, storage);
                    isExit = exitCommand.isExit();
                    break;
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
}
