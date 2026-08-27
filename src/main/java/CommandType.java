/**
 * Identifies the commands that Nova can execute.
 */
public enum CommandType {
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    ON("on", true),
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
     * Returns whether a complete command uses this command type.
     *
     * @param command trimmed command entered by the user
     * @return {@code true} if the keyword and argument form match
     */
    boolean matches(String command) {
        boolean isExactMatch = command.equals(keyword);
        boolean isCommandWithArguments = acceptsArguments
                && command.startsWith(keyword + " ");
        return isExactMatch || isCommandWithArguments;
    }
}
