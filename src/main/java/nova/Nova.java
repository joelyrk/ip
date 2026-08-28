package nova;

import java.nio.file.Path;

import nova.command.Command;
import nova.exception.NovaException;
import nova.parser.Parser;
import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

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
            String fullCommand = ui.readCommand();
            ui.showSeparator();

            try {
                Command command = parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (NovaException e) {
                ui.showError(e);
            } finally {
                ui.showSeparator();
            }
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
