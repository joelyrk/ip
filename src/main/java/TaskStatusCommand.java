/**
 * Changes and persists a task's completion status.
 */
public abstract class TaskStatusCommand extends TaskCommand {
    private final boolean isDone;

    /**
     * Creates a status command for a task.
     *
     * @param taskNumber one-based number of the task to update
     * @param commandName command keyword used in error guidance
     * @param isDone desired completion state
     */
    protected TaskStatusCommand(int taskNumber, String commandName, boolean isDone) {
        super(taskNumber, commandName);
        this.isDone = isDone;
    }

    /**
     * Changes and saves the task status, restoring its previous status if saving fails.
     *
     * @param tasks task list to update
     * @param ui user interface used to display the confirmation
     * @param storage storage used to persist the updated list
     * @throws NovaException if the updated list cannot be saved
     */
    @Override
    public final void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        int taskIndex = resolveTaskIndex(tasks);
        Task task = tasks.get(taskIndex);
        boolean previousStatus = task.isDone();
        setStatus(tasks, taskIndex, isDone);

        try {
            storage.save(tasks.getTasks());
        } catch (NovaException e) {
            setStatus(tasks, taskIndex, previousStatus);
            throw e;
        }
        showConfirmation(ui, task);
    }

    /**
     * Displays the confirmation specific to the new completion state.
     *
     * @param ui user interface used to display the confirmation
     * @param task updated task
     */
    protected abstract void showConfirmation(Ui ui, Task task);

    private void setStatus(TaskList tasks, int taskIndex, boolean isDone) {
        if (isDone) {
            tasks.mark(taskIndex);
        } else {
            tasks.unmark(taskIndex);
        }
    }
}
