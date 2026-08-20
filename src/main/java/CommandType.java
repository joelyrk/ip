/**
 * Identifies the commands that Nova can execute.
 */
public enum CommandType {
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    BYE("bye", false);

    private final String keyword;
    private final boolean acceptsArguments;

    /**
     * Creates a command type with its user-facing keyword and argument policy.
     *
     * @param keyword word that starts this command
     * @param acceptsArguments whether text may follow the keyword
     */
    CommandType(String keyword, boolean acceptsArguments) {
        this.keyword = keyword;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Finds the type represented by a complete command string.
     *
     * @param command trimmed command entered by the user
     * @return matching command type
     * @throws NovaException if the command is blank or has an unknown form
     */
    public static CommandType from(String command) throws NovaException {
        if (command.isEmpty()) {
            throw new NovaException("You entered a blank command. Try todo, deadline, event, list, mark, "
                    + "unmark, delete, or bye.");
        }

        for (CommandType commandType : values()) {
            boolean isExactMatch = command.equals(commandType.keyword);
            boolean isCommandWithArguments = commandType.acceptsArguments
                    && command.startsWith(commandType.keyword + " ");
            if (isExactMatch || isCommandWithArguments) {
                return commandType;
            }
        }

        throw new NovaException("I don't recognize that command. Start with todo, deadline, event, "
                + "list, mark, unmark, delete, or bye.");
    }
}
