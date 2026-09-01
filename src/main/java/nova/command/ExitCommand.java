package nova.command;

import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Displays Nova's farewell and ends the command loop.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that exits Nova.
     */
    public ExitCommand() {
        // This command has no arguments to initialize.
    }

    /**
     * Displays Nova's farewell message.
     *
     * @param tasks unused because exiting does not inspect or change tasks.
     * @param ui user interface used to display the farewell.
     * @param storage unused because exiting does not change persistent data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates that Nova should stop after this command.
     *
     * @return always {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
