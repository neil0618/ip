package anson;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for the Anson task-tracking chatbot.
 */
public class Anson {
    private static final String LINE = "___________________________________";

    private static final String DEADLINE_BY_SEPARATOR = " /by ";
    private static final String EVENT_FROM_SEPARATOR = " /from ";
    private static final String EVENT_TO_SEPARATOR = " /to ";

    /**
     * Runs the Anson chatbot, reading commands from standard input until
     * the user enters "bye".
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ArrayList<Task> tasks = Storage.load();

        printGreeting();

        while (true) {
            String reply = scan.nextLine();
            System.out.println(LINE);

            if (reply.trim().equals("bye")) {
                System.out.println("Bye, see you soon!");
                System.out.println(LINE);
                break;
            }

            handleCommand(reply, tasks);
        }
    }

    /**
     * Prints the chatbot's startup banner and greeting.
     */
    private static void printGreeting() {
        String banner = "    _                              \n"
                + "   / \\    _ __   ___   ___   _ __  \n"
                + "  / _ \\  | '_ \\ / __| / _ \\ | '_ \\ \n"
                + " / ___ \\ | | | |\\__ \\| (_) || | | |\n"
                + "/_/   \\_\\|_| |_||___/ \\___/ |_| |_|\n";

        System.out.println(banner);
        System.out.println("Hey there, I am Anson!");
        System.out.println("How can I help?");
        System.out.println(LINE);
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
    private static void handleCommand(String reply, ArrayList<Task> tasks) {
        try {
            String trimmed = reply.trim();
            if (trimmed.isEmpty()) {
                throw new AnsonException("Please enter a command.");
            }

            String[] split = trimmed.split(" ", 2);
            String commandWord = split[0];
            String arguments = split.length > 1 ? split[1].trim() : "";

            if (arguments.contains("|")) {
                throw new AnsonException("Sorry, the '|' character can't be used in a command!");
            }

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
                    handleList(tasks);
                    isModified = false;
                    break;
                case "todo":
                    handleTodo(tasks, arguments);
                    break;
                case "deadline":
                    handleDeadline(tasks, arguments);
                    break;
                case "event":
                    handleEvent(tasks, arguments);
                    break;
                default:
                    throw new AnsonException("I'm sorry, but I don't know what that means :(");
            }

            if (isModified) {
                Storage.save(tasks);
            }
        } catch (AnsonException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(LINE);
    }

    /**
     * Marks the task referenced by the given argument as done.
     *
     * @param tasks Current list of tasks.
     * @param arguments Text after the "mark" command word.
     * @throws AnsonException If the argument is missing, non-numeric, or out of range.
     */
    private static void handleMark(ArrayList<Task> tasks, String arguments) throws AnsonException {
        int taskNum = parseTaskNumber(arguments, tasks.size());

        Task task = tasks.get(taskNum - 1);
        task.markAsDone();
        System.out.println("I have marked this task as done:");
        System.out.println(task);
    }

    /**
     * Marks the task referenced by the given argument as not done.
     *
     * @param tasks Current list of tasks.
     * @param arguments Text after the "unmark" command word.
     * @throws AnsonException If the argument is missing, non-numeric, or out of range.
     */
    private static void handleUnmark(ArrayList<Task> tasks, String arguments) throws AnsonException {
        int taskNum = parseTaskNumber(arguments, tasks.size());

        Task task = tasks.get(taskNum - 1);
        task.markAsNotDone();
        System.out.println("I have marked this task as not done yet:");
        System.out.println(task);
    }

    /**
     * Deletes the task referenced by the given argument from the list.
     *
     * @param tasks Current list of tasks.
     * @param arguments Text after the "delete" command word.
     * @throws AnsonException If the argument is missing, non-numeric, or out of range.
     */
    private static void handleDelete(ArrayList<Task> tasks, String arguments) throws AnsonException {
        int taskNum = parseTaskNumber(arguments, tasks.size());

        Task removed = tasks.remove(taskNum - 1);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removed);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Parses and validates a task number given as an argument to
     * "mark"/"unmark"/"delete".
     *
     * @param arguments Raw text expected to be a single task number.
     * @param tasksCount Current number of tasks stored.
     * @return The validated, 1-based task number.
     * @throws AnsonException If the argument is missing, non-numeric, or out of range.
     */
    private static int parseTaskNumber(String arguments, int tasksCount) throws AnsonException {
        if (arguments.isEmpty()) {
            throw new AnsonException("Please specify the task number, e.g., mark 2.");
        }

        int taskNum;
        try {
            taskNum = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new AnsonException("The task number must be a whole number, e.g., mark 2.");
        }

        if (tasksCount == 0) {
            throw new AnsonException("Your task list is empty, so there is no task " + taskNum + ".");
        }

        if (taskNum <= 0 || taskNum > tasksCount) {
            throw new AnsonException("The task number must be between 1 and " + tasksCount + " inclusive.");
        }

        return taskNum;
    }

    /**
     * Prints every task currently stored, in order.
     *
     * @param tasks Current list of tasks.
     */
    private static void handleList(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Your task list is empty!");
            return;
        }

        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Parses a "todo" command's arguments and adds the resulting task.
     *
     * @param tasks Current list of tasks.
     * @param arguments Text after the "todo" command word.
     * @throws AnsonException If the description is empty.
     */
    private static void handleTodo(ArrayList<Task> tasks, String arguments) throws AnsonException {
        if (arguments.isEmpty()) {
            throw new AnsonException("The description of a todo cannot be empty.");
        }

        addTask(tasks, new Todo(arguments));
    }

    /**
     * Parses a "deadline" command's arguments and adds the resulting task.
     *
     * @param tasks Current list of tasks.
     * @param arguments Text after the "deadline" command word.
     * @throws AnsonException If the description or "/by" date/time is missing/empty.
     */
    private static void handleDeadline(ArrayList<Task> tasks, String arguments) throws AnsonException {
        if (arguments.isEmpty()) {
            throw new AnsonException("The description of a deadline cannot be empty.");
        }

        if (!arguments.contains(DEADLINE_BY_SEPARATOR)) {
            throw new AnsonException(
                    "Please include '/by' followed by a date/time, e.g., deadline return book /by Sunday.");
        }

        String[] parts = arguments.split(DEADLINE_BY_SEPARATOR, 2);
        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty()) {
            throw new AnsonException("The description of a deadline cannot be empty.");
        }

        if (by.isEmpty()) {
            throw new AnsonException("Please specify a date/time after '/by'.");
        }

        addTask(tasks, new Deadline(description, by));
    }

    /**
     * Parses an "event" command's arguments and adds the resulting task.
     *
     * @param tasks Current list of tasks.
     * @param arguments Text after the "event" command word.
     * @throws AnsonException If the description, "/from", or "/to" date/time is missing/empty.
     */
    private static void handleEvent(ArrayList<Task> tasks, String arguments) throws AnsonException {
        if (arguments.isEmpty()) {
            throw new AnsonException("The description of an event cannot be empty.");
        }

        if (!arguments.contains(EVENT_FROM_SEPARATOR)) {
            throw new AnsonException(
                    "Please include '/from' followed by a start date/time, "
                            + "e.g., event meeting /from Mon 2pm /to 4pm.");
        }

        String[] fromSplit = arguments.split(EVENT_FROM_SEPARATOR, 2);
        String description = fromSplit[0].trim();

        if (description.isEmpty()) {
            throw new AnsonException("The description of an event cannot be empty.");
        }

        String remainder = fromSplit[1];
        if (!remainder.contains(EVENT_TO_SEPARATOR)) {
            throw new AnsonException(
                    "Please include '/to' followed by an end date/time, "
                            + "e.g., event meeting /from Mon 2pm /to 4pm.");
        }

        String[] toSplit = remainder.split(EVENT_TO_SEPARATOR, 2);
        String from = toSplit[0].trim();
        String to = toSplit[1].trim();

        if (from.isEmpty()) {
            throw new AnsonException("Please specify a start date/time after '/from'.");
        }

        if (to.isEmpty()) {
            throw new AnsonException("Please specify an end date/time after '/to'.");
        }

        addTask(tasks, new Event(description, from, to));
    }

    /**
     * Stores a new task and prints the standard confirmation message.
     *
     * @param tasks Current list of tasks.
     * @param newTask Task to add.
     */
    private static void addTask(ArrayList<Task> tasks, Task newTask) {
        tasks.add(newTask);

        System.out.println("Got it! I've added this task:");
        System.out.println("  " + newTask);
        System.out.println("Number of tasks in the list: " + tasks.size());
    }
}