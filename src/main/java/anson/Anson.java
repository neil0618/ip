package anson;

import java.util.Scanner;

/**
 * Entry point for the Anson task-tracking chatbot.
 */
public class Anson {
    private static final int MAX_TASKS = 100;
    private static final String LINE = "___________________________________";

    private static final String MARK_PREFIX = "mark ";
    private static final String UNMARK_PREFIX = "unmark ";
    private static final String TODO_PREFIX = "todo ";
    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String EVENT_PREFIX = "event ";

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
        Task[] tasks = new Task[MAX_TASKS];
        int tasksCount = 0;

        printGreeting();

        while (true) {
            String reply = scan.nextLine();
            System.out.println(LINE);

            if (reply.equals("bye")) {
                System.out.println("Bye, see you soon!");
                System.out.println(LINE);
                break;
            }

            tasksCount = handleCommand(reply, tasks, tasksCount);
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
     * handler and prints the trailing separator line.
     *
     * @param reply Raw command line entered by the user.
     * @param tasks Current task array.
     * @param tasksCount Current number of tasks stored.
     * @return Updated task count after the command has been handled.
     */
    private static int handleCommand(String reply, Task[] tasks, int tasksCount) {
        if (reply.startsWith(MARK_PREFIX)) {
            handleMark(tasks, tasksCount, reply);
        } else if (reply.startsWith(UNMARK_PREFIX)) {
            handleUnmark(tasks, tasksCount, reply);
        } else if (reply.equals("list")) {
            handleList(tasks, tasksCount);
        } else if (reply.startsWith(TODO_PREFIX)) {
            tasksCount = handleTodo(tasks, tasksCount, reply);
        } else if (reply.startsWith(DEADLINE_PREFIX)) {
            tasksCount = handleDeadline(tasks, tasksCount, reply);
        } else if (reply.startsWith(EVENT_PREFIX)) {
            tasksCount = handleEvent(tasks, tasksCount, reply);
        } else {
            System.out.println("Please specify the task as todo, deadline, or event!");
        }

        System.out.println(LINE);
        return tasksCount;
    }

    /**
     * Marks the task referenced in the given command as done.
     *
     * @param tasks Current task array.
     * @param tasksCount Current number of tasks stored.
     * @param reply Raw command line, expected to start with "mark ".
     */
    private static void handleMark(Task[] tasks, int tasksCount, String reply) {
        int taskNum = Integer.parseInt(reply.substring(MARK_PREFIX.length()));

        if (taskNum <= 0 || taskNum > tasksCount) {
            System.out.println("That task number does not exist!");
            return;
        }

        tasks[taskNum - 1].markAsDone();
        System.out.println("I have marked this task as done:");
        System.out.println(tasks[taskNum - 1]);
    }

    /**
     * Marks the task referenced in the given command as not done.
     *
     * @param tasks Current task array.
     * @param tasksCount Current number of tasks stored.
     * @param reply Raw command line, expected to start with "unmark ".
     */
    private static void handleUnmark(Task[] tasks, int tasksCount, String reply) {
        int taskNum = Integer.parseInt(reply.substring(UNMARK_PREFIX.length()));

        if (taskNum <= 0 || taskNum > tasksCount) {
            System.out.println("That task number does not exist!");
            return;
        }

        tasks[taskNum - 1].markAsNotDone();
        System.out.println("I have marked this task as not done yet:");
        System.out.println(tasks[taskNum - 1]);
    }

    /**
     * Prints every task currently stored, in order.
     *
     * @param tasks Current task array.
     * @param tasksCount Current number of tasks stored.
     */
    private static void handleList(Task[] tasks, int tasksCount) {
        if (tasksCount == 0) {
            System.out.println("Your task list is empty!");
            return;
        }

        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasksCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
    }

    /**
     * Parses a "todo" command and adds the resulting task.
     *
     * @param tasks Current task array.
     * @param tasksCount Current number of tasks stored.
     * @param reply Raw command line, expected to start with "todo ".
     * @return Updated task count.
     */
    private static int handleTodo(Task[] tasks, int tasksCount, String reply) {
        String description = reply.substring(TODO_PREFIX.length());
        return addTask(tasks, tasksCount, new Todo(description));
    }

    /**
     * Parses a "deadline" command and adds the resulting task.
     *
     * @param tasks Current task array.
     * @param tasksCount Current number of tasks stored.
     * @param reply Raw command line, expected to start with "deadline ".
     * @return Updated task count.
     */
    private static int handleDeadline(Task[] tasks, int tasksCount, String reply) {
        String[] parts = reply.substring(DEADLINE_PREFIX.length()).split(DEADLINE_BY_SEPARATOR, 2);
        return addTask(tasks, tasksCount, new Deadline(parts[0], parts[1]));
    }

    /**
     * Parses an "event" command and adds the resulting task.
     *
     * @param tasks Current task array.
     * @param tasksCount Current number of tasks stored.
     * @param reply Raw command line, expected to start with "event ".
     * @return Updated task count.
     */
    private static int handleEvent(Task[] tasks, int tasksCount, String reply) {
        String[] fromSplit = reply.substring(EVENT_PREFIX.length()).split(EVENT_FROM_SEPARATOR, 2);
        String[] toSplit = fromSplit[1].split(EVENT_TO_SEPARATOR, 2);
        return addTask(tasks, tasksCount, new Event(fromSplit[0], toSplit[0], toSplit[1]));
    }

    /**
     * Stores a new task and prints the standard confirmation message.
     *
     * @param tasks Current task array.
     * @param tasksCount Current number of tasks stored.
     * @param newTask Task to add.
     * @return Updated task count.
     */
    private static int addTask(Task[] tasks, int tasksCount, Task newTask) {
        tasks[tasksCount] = newTask;
        tasksCount++;

        System.out.println("Got it! I've added this task:");
        System.out.println("  " + newTask);
        System.out.println("Number of tasks in the list: " + tasksCount);

        return tasksCount;
    }
}