/**
 * Deletes a task and persists the updated task list.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that deletes the selected task.
     *
     * @param taskIndex zero-based index of the task to delete
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Deletes and saves the task, restoring it at its original index if saving fails.
     *
     * @param tasks task list to update
     * @param ui user interface used to display the confirmation
     * @param storage storage used to persist the updated list
     * @throws NovaException if the updated list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        Task removedTask = tasks.remove(taskIndex);
        try {
            storage.save(tasks.getTasks());
        } catch (NovaException e) {
            tasks.add(taskIndex, removedTask);
            throw e;
        }
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
