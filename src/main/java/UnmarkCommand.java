/**
 * Marks a task as not completed.
 */
public class UnmarkCommand extends TaskStatusCommand {
    /**
     * Creates a command that marks the selected task as not completed.
     *
     * @param taskIndex zero-based index of the task to unmark
     */
    public UnmarkCommand(int taskIndex) {
        super(taskIndex, false);
    }

    @Override
    protected void showConfirmation(Ui ui, Task task) {
        ui.showTaskUnmarked(task);
    }
}
