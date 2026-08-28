/**
 * Marks a task as completed.
 */
public class MarkCommand extends TaskStatusCommand {
    /**
     * Creates a command that marks the selected task as completed.
     *
     * @param taskNumber one-based number of the task to mark
     */
    public MarkCommand(int taskNumber) {
        super(taskNumber, "mark", true);
    }

    @Override
    protected void showConfirmation(Ui ui, Task task) {
        ui.showTaskMarked(task);
    }
}
