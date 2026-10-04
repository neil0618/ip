package anson;

/**
 * Entry point for the Anson task-tracking chatbot.
 */
public class Anson {
    private static final Ui ui = new Ui();
    private static final Storage storage = new Storage("data/anson.txt");

    /**
     * Runs the Anson chatbot, reading commands from standard input until
     * the user enters "bye".
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        ui.showWelcome();

        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
            if (storage.getSkippedLineCount() > 0) {
                ui.showSkippedLines(storage.getSkippedLineCount());
            }
        } catch (AnsonException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }

        while (true) {
            String reply = ui.readCommand();
            ui.showLine();

            if (reply.trim().equals("bye")) {
                ui.showGoodbye();
                break;
            }

            handleCommand(reply, tasks);
        }
    }

    /**
     * Dispatches a single line of user input to the matching command
     * handler and prints the trailing separator line. Any AnsonException
     * raised while handling the command is caught here so the chatbot
     * never crashes mid-session.
     *
     * @param reply Raw command line entered by the user.
     * @param tasks Current list of tasks.
     */
    private static void handleCommand(String reply, TaskList tasks) {
        try {
            String commandWord = Parser.parseCommandWord(reply);
            String arguments = Parser.parseArguments(reply);

            boolean isModified = true;

            switch (commandWord) {
                case "mark":
                    handleMark(tasks, arguments);
                    break;
                case "unmark":
                    handleUnmark(tasks, arguments);
                    break;
                case "delete":
                    handleDelete(tasks, arguments);
                    break;
                case "list":
                    ui.showTaskList(tasks);
                    isModified = false;
                    break;
                case "todo":
                    addTask(tasks, Parser.parseTodo(arguments));
                    break;
                case "deadline":
                    addTask(tasks, Parser.parseDeadline(arguments));
                    break;
                case "event":
                    addTask(tasks, Parser.parseEvent(arguments));
                    break;
                default:
                    throw new AnsonException("I'm sorry, but I don't know what that means :(");
            }

            if (isModified) {
                storage.save(tasks);
            }
        } catch (AnsonException e) {
            ui.showError(e.getMessage());
        }

        ui.showLine();
    }

    /**
     * Marks the task referenced by the given argument as done.
     *
     * @param tasks Current list of tasks.
     * @param arguments Text after the "mark" command word.
     * @throws AnsonException If the argument is missing, non-numeric, or out of range.
     */
    private static void handleMark(TaskList tasks, String arguments) throws AnsonException {
        int taskNum = Parser.parseTaskNumber(arguments, tasks.size());

        Task task = tasks.get(taskNum - 1);
        task.markAsDone();
        ui.showTaskMarked(task);
    }

    /**
     * Marks the task referenced by the given argument as not done.
     *
     * @param tasks Current list of tasks.
     * @param arguments Text after the "unmark" command word.
     * @throws AnsonException If the argument is missing, non-numeric, or out of range.
     */
    private static void handleUnmark(TaskList tasks, String arguments) throws AnsonException {
        int taskNum = Parser.parseTaskNumber(arguments, tasks.size());

        Task task = tasks.get(taskNum - 1);
        task.markAsNotDone();
        ui.showTaskUnmarked(task);
    }

    /**
     * Deletes the task referenced by the given argument from the list.
     *
     * @param tasks Current list of tasks.
     * @param arguments Text after the "delete" command word.
     * @throws AnsonException If the argument is missing, non-numeric, or out of range.
     */
    private static void handleDelete(TaskList tasks, String arguments) throws AnsonException {
        int taskNum = Parser.parseTaskNumber(arguments, tasks.size());

        Task removed = tasks.remove(taskNum - 1);
        ui.showTaskDeleted(removed, tasks.size());
    }

    /**
     * Adds a task to the list and shows the confirmation message.
     *
     * @param tasks Current list of tasks.
     * @param newTask Task to add.
     */
    private static void addTask(TaskList tasks, Task newTask) {
        tasks.add(newTask);
        ui.showTaskAdded(newTask, tasks.size());
    }
}