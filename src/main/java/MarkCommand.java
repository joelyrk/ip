/**
 * Marks a task as completed.
 */
public class MarkCommand extends TaskStatusCommand {
    /**
     * Creates a command that marks the selected task as completed.
     *
     * @param taskIndex zero-based index of the task to mark
     */
    public MarkCommand(int taskIndex) {
        super(taskIndex, true);
    }

    @Override
    protected void showConfirmation(Ui ui, Task task) {
        ui.showTaskMarked(task);
    }
}
